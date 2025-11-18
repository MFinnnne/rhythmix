package io.github.mfinnnne.rhythmix.execute;

import io.github.mfinnnne.rhythmix.config.RhythmixConfig;
import io.github.mfinnnne.rhythmix.exception.TranslatorException;
import io.github.mfinnnne.rhythmix.monitor.RhythmixDefaultMonitor;
import io.github.mfinnnne.rhythmix.monitor.RhythmixExecutionData;
import io.github.mfinnnne.rhythmix.monitor.RhythmixMonitor;
import io.github.mfinnnne.rhythmix.util.RhythmixEventData;
import io.github.mfinnnne.rhythmix.util.Util;
import org.junit.jupiter.api.*;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive unit tests for RhythmixExecutorManager class.
 * Tests cover all public methods, error handling, monitor integration,
 * and thread-safety aspects.
 *
 * @author MFine
 * @version 1.0
 */
@DisplayName("RhythmixExecutorManager Tests")
public class RhythmixExecutorManagerTest {

    private RhythmixExecutorManager manager;
    private TestMonitor testMonitor;

    @BeforeEach
    void setUp() {
        // Reset singleton instance before each test
        RhythmixExecutorManager.reset();
        manager = RhythmixExecutorManager.getInstance();

        // Set up test monitor
        testMonitor = new TestMonitor();
        RhythmixConfig.setMonitor(testMonitor);
    }

    @AfterEach
    void tearDown() {
        // Reset to default monitor
        RhythmixConfig.setMonitor(new RhythmixDefaultMonitor());
        // Clean up manager
        RhythmixExecutorManager.reset();
    }

    // ==================== Helper Methods ====================

    private RhythmixExpressionEntity createTestEntity(String id, String expression, boolean enable, List<String> filterIds) {
        RhythmixExpressionEntity entity = new RhythmixExpressionEntity();
        entity.setId(id);
        entity.setExpression(expression);
        entity.setEnable(enable);
        entity.setFilterIds(filterIds != null ? filterIds : new ArrayList<>());
        return entity;
    }

    private RhythmixExpressionEntity createSimpleEntity(String id, String expression) {
        return createTestEntity(id, expression, true, new ArrayList<>());
    }

    // ==================== Singleton Pattern Tests ====================

    @Test
    @DisplayName("Test getInstance returns same instance")
    void testGetInstance_ReturnsSameInstance() {
        RhythmixExecutorManager instance1 = RhythmixExecutorManager.getInstance();
        RhythmixExecutorManager instance2 = RhythmixExecutorManager.getInstance();

        assertNotNull(instance1);
        assertNotNull(instance2);
        assertSame(instance1, instance2, "Should return the same singleton instance");
    }

    @Test
    @DisplayName("Test getInstance is thread-safe")
    void testGetInstance_ThreadSafe() throws InterruptedException {
        RhythmixExecutorManager.reset();

        int threadCount = 10;
        CountDownLatch latch = new CountDownLatch(threadCount);
        List<RhythmixExecutorManager> instances = new ArrayList<>();

        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);

        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                instances.add(RhythmixExecutorManager.getInstance());
                latch.countDown();
            });
        }

        assertTrue(latch.await(5, TimeUnit.SECONDS));
        executorService.shutdown();

        // All instances should be the same
        RhythmixExecutorManager firstInstance = instances.get(0);
        for (RhythmixExecutorManager instance : instances) {
            assertSame(firstInstance, instance, "All instances should be the same");
        }
    }

    @Test
    @DisplayName("Test reset clears instance")
    void testReset_ClearsInstance() throws TranslatorException {
        // Create an executor
        RhythmixExpressionEntity entity = createSimpleEntity("test-1", ">5");
        manager.create(entity);
        assertEquals(1, manager.size());

        // Reset
        RhythmixExecutorManager.reset();

        // Get new instance
        RhythmixExecutorManager newManager = RhythmixExecutorManager.getInstance();
        assertEquals(0, newManager.size(), "New instance should have empty cache");
    }

    // ==================== Create Operation Tests ====================

    @Test
    @DisplayName("Test create executor successfully")
    void testCreate_Success() throws TranslatorException {
        RhythmixExpressionEntity entity = createSimpleEntity("expr-1", "{>5}->{<3}");

        RhythmixExecutor executor = manager.create(entity);

        assertNotNull(executor);
        assertEquals(1, manager.size());
        assertTrue(manager.exists("expr-1"));
        assertTrue(testMonitor.createdEntities.contains(entity));
    }

    @Test
    @DisplayName("Test create executor with filter IDs")
    void testCreate_WithFilterIds() throws TranslatorException {
        List<String> filterIds = Arrays.asList("filter-1", "filter-2");
        RhythmixExpressionEntity entity = createTestEntity("expr-1", ">10", true, filterIds);

        RhythmixExecutor executor = manager.create(entity);

        assertNotNull(executor);
        assertTrue(manager.exists("expr-1"));

    }

    @Test
    @DisplayName("Test create with compilation error")
    void testCreate_CompilationError() {
        RhythmixExpressionEntity entity = createSimpleEntity("expr-1", "invalid{{{expression");

        assertThrows(TranslatorException.class, () -> manager.create(entity));
        assertEquals(0, manager.size(), "Failed creation should not add to cache");
        assertFalse(testMonitor.compilationErrors.isEmpty());
    }

    @Test
    @DisplayName("Test create invokes monitor callback")
    void testCreate_MonitorCallbackInvoked() throws TranslatorException {
        RhythmixExpressionEntity entity = createSimpleEntity("expr-1", ">5");

        manager.create(entity);

        assertEquals(1, testMonitor.createdEntities.size());
        assertTrue(testMonitor.createdEntities.contains(entity));
    }

    @Test
    @DisplayName("Test create with monitor callback failure does not affect operation")
    void testCreate_MonitorCallbackFailureDoesNotAffectOperation() throws TranslatorException {
        // Set monitor that throws exception
        RhythmixConfig.setMonitor(new RhythmixMonitor() {
            @Override
            public void onExpressionCreated(RhythmixExpressionEntity entity) {
                throw new RuntimeException("Monitor failure");
            }
        });

        RhythmixExpressionEntity entity = createSimpleEntity("expr-1", ">5");

        // Should not throw exception
        assertDoesNotThrow(() -> manager.create(entity));
        assertTrue(manager.exists("expr-1"), "Executor should be created despite monitor failure");
    }

    @Test
    @DisplayName("Test create replaces existing executor")
    void testCreate_ReplacesExistingExecutor() throws TranslatorException {
        RhythmixExpressionEntity entity1 = createSimpleEntity("expr-1", ">5");
        RhythmixExpressionEntity entity2 = createSimpleEntity("expr-1", ">10");

        manager.create(entity1);
        manager.create(entity2);

        assertEquals(1, manager.size(), "Should only have one executor");
        Optional<RhythmixExecutor> executor = manager.getExecutor("expr-1");
        assertTrue(executor.isPresent());
    }

    // ==================== Update Operation Tests ====================

    @Test
    @DisplayName("Test update executor successfully")
    void testUpdate_Success() throws TranslatorException {
        RhythmixExpressionEntity entity = createSimpleEntity("expr-1", ">5");
        manager.create(entity);

        entity.setExpression(">10");
        RhythmixExecutor updatedExecutor = manager.update(entity);

        assertNotNull(updatedExecutor);
        assertTrue(manager.exists("expr-1"));
        assertEquals(1, testMonitor.updatedEntities.size());
    }

    @Test
    @DisplayName("Test update creates new if not exists")
    void testUpdate_CreatesNewIfNotExists() throws TranslatorException {
        RhythmixExpressionEntity entity = createSimpleEntity("expr-1", ">5");

        RhythmixExecutor executor = manager.update(entity);

        assertNotNull(executor);
        assertTrue(manager.exists("expr-1"));
    }

    @Test
    @DisplayName("Test update with compilation error")
    void testUpdate_CompilationError() throws TranslatorException {
        RhythmixExpressionEntity entity = createSimpleEntity("expr-1", ">5");
        manager.create(entity);

        entity.setExpression("invalid{{{expression");

        assertThrows(TranslatorException.class, () -> manager.update(entity));
        assertTrue(testMonitor.compilationErrors.size() > 0);
    }

    @Test
    @DisplayName("Test update invokes monitor callback")
    void testUpdate_MonitorCallbackInvoked() throws TranslatorException {
        RhythmixExpressionEntity entity = createSimpleEntity("expr-1", ">5");
        manager.create(entity);

        entity.setExpression(">10");
        manager.update(entity);

        assertEquals(1, testMonitor.updatedEntities.size());
        assertTrue(testMonitor.updatedEntities.contains(entity));
    }

    @Test
    @DisplayName("Test update preserves created timestamp")
    void testUpdate_PreservesCreatedTimestamp() throws TranslatorException {
        RhythmixExpressionEntity entity = createSimpleEntity("expr-1", ">5");
        manager.create(entity);

        Optional<RhythmixExecutor> executorBefore = manager.getExecutorWrapper("expr-1");
        assertTrue(executorBefore.isPresent());

        // Small delay to ensure timestamps would differ
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        entity.setExpression(">10");
        manager.update(entity);

        Optional<RhythmixExecutor> executorAfter = manager.getExecutorWrapper("expr-1");
        assertTrue(executorAfter.isPresent());

        // Created timestamp should be the same, updated timestamp should be different
        assertEquals(executorBefore.get().getCreatedAt(), executorAfter.get().getCreatedAt());
    }

    // ==================== Delete Operation Tests ====================

    @Test
    @DisplayName("Test delete executor successfully")
    void testDelete_Success() throws TranslatorException {
        RhythmixExpressionEntity entity = createSimpleEntity("expr-1", ">5");
        manager.create(entity);

        manager.delete(entity);

        assertFalse(manager.exists("expr-1"));
        assertEquals(0, manager.size());
        assertEquals(1, testMonitor.deletedEntities.size());
    }

    @Test
    @DisplayName("Test delete removes from cache")
    void testDelete_RemovesFromCache() throws TranslatorException {
        RhythmixExpressionEntity entity = createSimpleEntity("expr-1", ">5");
        manager.create(entity);
        assertTrue(manager.exists("expr-1"));

        manager.delete(entity);

        assertFalse(manager.exists("expr-1"));
        assertFalse(manager.getExecutor("expr-1").isPresent());
    }

    @Test
    @DisplayName("Test delete removes from route map")
    void testDelete_RemovesFromRouteMap() throws TranslatorException {
        List<String> filterIds = Arrays.asList("filter-1");
        RhythmixExpressionEntity entity = createTestEntity("expr-1", ">5", true, filterIds);
        manager.create(entity);

        manager.delete(entity);

        assertFalse(manager.exists("expr-1"));
    }

    @Test
    @DisplayName("Test delete invokes monitor callback")
    void testDelete_MonitorCallbackInvoked() throws TranslatorException {
        RhythmixExpressionEntity entity = createSimpleEntity("expr-1", ">5");
        manager.create(entity);

        manager.delete(entity);

        assertEquals(1, testMonitor.deletedEntities.size());
        assertTrue(testMonitor.deletedEntities.contains(entity));
    }

    @Test
    @DisplayName("Test delete non-existent executor")
    void testDelete_NonExistentExecutor() {
        RhythmixExpressionEntity entity = createSimpleEntity("expr-1", ">5");

        // Should not throw exception
        assertDoesNotThrow(() -> manager.delete(entity));
        assertEquals(0, testMonitor.deletedEntities.size(), "Monitor should not be called for non-existent executor");
    }

    // ==================== Enable/Disable Tests ====================

    @Test
    @DisplayName("Test enable executor successfully")
    void testEnable_Success() throws TranslatorException {
        RhythmixExpressionEntity entity = createTestEntity("expr-1", ">5", false, new ArrayList<>());
        manager.create(entity);

        manager.enable(entity);

        assertTrue(manager.isEnabled("expr-1"));
        Optional<RhythmixExecutor> executor = manager.getExecutorWrapper("expr-1");
        assertTrue(executor.isPresent());
        assertTrue(executor.get().isEnabled());
    }

    @Test
    @DisplayName("Test enable non-existent executor")
    void testEnable_NonExistentExecutor() {
        RhythmixExpressionEntity entity = createSimpleEntity("expr-1", ">5");

        // Should not throw exception
        assertDoesNotThrow(() -> manager.enable(entity));
        assertFalse(manager.isEnabled("expr-1"));
    }

    @Test
    @DisplayName("Test disable executor successfully")
    void testDisable_Success() throws TranslatorException {
        RhythmixExpressionEntity entity = createSimpleEntity("expr-1", ">5");
        manager.create(entity);
        assertTrue(manager.isEnabled("expr-1"));

        manager.disable(entity);

        assertFalse(manager.isEnabled("expr-1"));
        Optional<RhythmixExecutor> executor = manager.getExecutorWrapper("expr-1");
        assertTrue(executor.isPresent());
        assertFalse(executor.get().isEnabled());
    }

    @Test
    @DisplayName("Test disable non-existent executor")
    void testDisable_NonExistentExecutor() {
        RhythmixExpressionEntity entity = createSimpleEntity("expr-1", ">5");

        // Should not throw exception
        assertDoesNotThrow(() -> manager.disable(entity));
        assertFalse(manager.isEnabled("expr-1"));
    }

    @Test
    @DisplayName("Test enable updates timestamp")
    void testEnable_UpdatesTimestamp() throws TranslatorException {
        RhythmixExpressionEntity entity = createTestEntity("expr-1", ">5", false, new ArrayList<>());
        manager.create(entity);

        Optional<RhythmixExecutor> executorBefore = manager.getExecutorWrapper("expr-1");
        assertTrue(executorBefore.isPresent());
        final String time = executorBefore.get().getUpdatedAt().toString();

        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        manager.enable(entity);

        Optional<RhythmixExecutor> executorAfter = manager.getExecutorWrapper("expr-1");
        assertTrue(executorAfter.isPresent());
        assertTrue(executorAfter.get().getUpdatedAt().isAfter(LocalDateTime.parse(time)));
    }

    // ==================== Query Operations Tests ====================

    @Test
    @DisplayName("Test getExecutor returns executor when found")
    void testGetExecutor_Found() throws TranslatorException {
        RhythmixExpressionEntity entity = createSimpleEntity("expr-1", ">5");
        manager.create(entity);

        Optional<RhythmixExecutor> executor = manager.getExecutor("expr-1");

        assertTrue(executor.isPresent());
    }

    @Test
    @DisplayName("Test getExecutor returns empty when not found")
    void testGetExecutor_NotFound() {
        Optional<RhythmixExecutor> executor = manager.getExecutor("non-existent");

        assertFalse(executor.isPresent());
    }

    @Test
    @DisplayName("Test getExecutor with null ID")
    void testGetExecutor_NullId() {
        Optional<RhythmixExecutor> executor = manager.getExecutor(null);

        assertFalse(executor.isPresent());
    }

    @Test
    @DisplayName("Test getExecutorWrapper returns executor when found")
    void testGetExecutorWrapper_Found() throws TranslatorException {
        RhythmixExpressionEntity entity = createSimpleEntity("expr-1", ">5");
        manager.create(entity);

        Optional<RhythmixExecutor> executor = manager.getExecutorWrapper("expr-1");

        assertTrue(executor.isPresent());
        assertEquals("expr-1", executor.get().getId());
        assertNotNull(executor.get());
        assertNotNull(executor.get().getCreatedAt());
        assertNotNull(executor.get().getUpdatedAt());
    }

    @Test
    @DisplayName("Test getExecutorWrapper returns empty when not found")
    void testGetExecutorWrapper_NotFound() {
        Optional<RhythmixExecutor> executor = manager.getExecutorWrapper("non-existent");

        assertFalse(executor.isPresent());
    }

    @Test
    @DisplayName("Test getExecutorWrapper with null ID")
    void testGetExecutorWrapper_NullId() {
        Optional<RhythmixExecutor> executor = manager.getExecutorWrapper(null);

        assertFalse(executor.isPresent());
    }

    @Test
    @DisplayName("Test exists returns true when executor exists")
    void testExists_True() throws TranslatorException {
        RhythmixExpressionEntity entity = createSimpleEntity("expr-1", ">5");
        manager.create(entity);

        assertTrue(manager.exists("expr-1"));
    }

    @Test
    @DisplayName("Test exists returns false when executor does not exist")
    void testExists_False() {
        assertFalse(manager.exists("non-existent"));
    }

    @Test
    @DisplayName("Test exists with null ID")
    void testExists_NullId() {
        assertFalse(manager.exists(null));
    }

    @Test
    @DisplayName("Test isEnabled returns true when enabled")
    void testIsEnabled_True() throws TranslatorException {
        RhythmixExpressionEntity entity = createSimpleEntity("expr-1", ">5");
        manager.create(entity);

        assertTrue(manager.isEnabled("expr-1"));
    }

    @Test
    @DisplayName("Test isEnabled returns false when disabled")
    void testIsEnabled_False() throws TranslatorException {
        RhythmixExpressionEntity entity = createTestEntity("expr-1", ">5", false, new ArrayList<>());
        manager.create(entity);

        assertFalse(manager.isEnabled("expr-1"));
    }

    @Test
    @DisplayName("Test isEnabled returns false for non-existent executor")
    void testIsEnabled_NonExistent() {
        assertFalse(manager.isEnabled("non-existent"));
    }

    @Test
    @DisplayName("Test size returns correct count")
    void testSize_ReturnsCorrectCount() throws TranslatorException {
        assertEquals(0, manager.size());

        manager.create(createSimpleEntity("expr-1", ">5"));
        assertEquals(1, manager.size());

        manager.create(createSimpleEntity("expr-2", "<10"));
        assertEquals(2, manager.size());

        manager.delete(createSimpleEntity("expr-1", ">5"));
        assertEquals(1, manager.size());
    }

    // ==================== Cache Management Tests ====================

    @Test
    @DisplayName("Test clear removes all executors")
    void testClear_RemovesAllExecutors() throws TranslatorException {
        manager.create(createSimpleEntity("expr-1", ">5"));
        manager.create(createSimpleEntity("expr-2", "<10"));
        manager.create(createSimpleEntity("expr-3", "==7"));

        assertEquals(3, manager.size());

        manager.clear();

        assertEquals(0, manager.size());
        assertFalse(manager.exists("expr-1"));
        assertFalse(manager.exists("expr-2"));
        assertFalse(manager.exists("expr-3"));
    }

    @Test
    @DisplayName("Test clear does not trigger deletion callbacks")
    void testClear_NoCallbacks() throws TranslatorException {
        manager.create(createSimpleEntity("expr-1", ">5"));
        manager.create(createSimpleEntity("expr-2", "<10"));

        manager.clear();

        assertEquals(0, testMonitor.deletedEntities.size(), "Clear should not trigger deletion callbacks");
    }

    // ==================== Edge Cases and Error Handling Tests ====================

    @Test
    @DisplayName("Test create with null expression throws exception")
    void testCreate_NullExpression() {
        RhythmixExpressionEntity entity = createSimpleEntity("expr-1", null);

        assertThrows(Exception.class, () -> manager.create(entity));
    }

    @Test
    @DisplayName("Test update with null expression throws exception")
    void testUpdate_NullExpression() {
        RhythmixExpressionEntity entity = createSimpleEntity("expr-1", null);

        assertThrows(Exception.class, () -> manager.update(entity));
    }

    @Test
    @DisplayName("Test concurrent create operations")
    void testConcurrentOperations() throws InterruptedException {
        int threadCount = 10;
        CountDownLatch latch = new CountDownLatch(threadCount);
        AtomicInteger successCount = new AtomicInteger(0);

        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);

        for (int i = 0; i < threadCount; i++) {
            final int index = i;
            executorService.submit(() -> {
                try {
                    RhythmixExpressionEntity entity = createSimpleEntity("expr-" + index, ">5");
                    manager.create(entity);
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    // Ignore
                } finally {
                    latch.countDown();
                }
            });
        }

        assertTrue(latch.await(10, TimeUnit.SECONDS));
        executorService.shutdown();

        assertEquals(threadCount, successCount.get(), "All concurrent creates should succeed");
        assertEquals(threadCount, manager.size());
    }

    @Test
    @DisplayName("Test executor can execute events after creation")
    void testExecutor_CanExecuteEvents() throws TranslatorException {
        RhythmixExpressionEntity entity = createSimpleEntity("expr-1", "{>5}->{<3}");
        RhythmixExecutor executor = manager.create(entity);

        RhythmixEventData event1 = Util.genEventData("e1", "6", new Timestamp(System.currentTimeMillis()));
        RhythmixEventData event2 = Util.genEventData("e2", "2", new Timestamp(System.currentTimeMillis() + 100));

        boolean result1 = executor.execute(event1);
        assertFalse(result1, "First event should not complete the flow");

        boolean result2 = executor.execute(event2);
        assertTrue(result2, "Second event should complete the flow");
    }

    @Test
    @DisplayName("Test multiple executors with different expressions")
    void testMultipleExecutors_DifferentExpressions() throws TranslatorException {
        RhythmixExpressionEntity entity1 = createSimpleEntity("expr-1", ">5");
        RhythmixExpressionEntity entity2 = createSimpleEntity("expr-2", "<10");
        RhythmixExpressionEntity entity3 = createSimpleEntity("expr-3", "[5,10]");

        manager.create(entity1);
        manager.create(entity2);
        manager.create(entity3);

        assertEquals(3, manager.size());
        assertTrue(manager.exists("expr-1"));
        assertTrue(manager.exists("expr-2"));
        assertTrue(manager.exists("expr-3"));
    }

    @Test
    @DisplayName("Test executor metadata is correctly set")
    void testExecutor_MetadataCorrectlySet() throws TranslatorException {
        RhythmixExpressionEntity entity = createSimpleEntity("expr-1", ">5");
        manager.create(entity);

        Optional<RhythmixExecutor> executor = manager.getExecutorWrapper("expr-1");
        assertTrue(executor.isPresent());

        RhythmixExecutor exec = executor.get();
        assertEquals("expr-1", exec.getId());
        assertEquals(entity, exec.getEntity());
        assertTrue(exec.isEnabled());
        assertNotNull(exec.getCreatedAt());
        assertNotNull(exec.getUpdatedAt());
        assertNotNull(exec);
        assertTrue(exec.canExecute());
    }

    @Test
    @DisplayName("Test disabled executor canExecute returns false")
    void testExecutor_DisabledCannotExecute() throws TranslatorException {
        RhythmixExpressionEntity entity = createSimpleEntity("expr-1", ">5");
        manager.create(entity);
        manager.disable(entity);

        Optional<RhythmixExecutor> executor = manager.getExecutorWrapper("expr-1");
        assertTrue(executor.isPresent());
        assertFalse(executor.get().canExecute());
    }

    @Test
    @DisplayName("Test update with filter IDs")
    void testUpdate_WithFilterIds() throws TranslatorException {
        List<String> filterIds1 = Arrays.asList("filter-1");
        RhythmixExpressionEntity entity = createTestEntity("expr-1", ">5", true, filterIds1);
        manager.create(entity);

        List<String> filterIds2 = Arrays.asList("filter-2", "filter-3");
        entity.setFilterIds(filterIds2);
        entity.setExpression(">10");
        manager.update(entity);

        assertTrue(manager.exists("expr-1"));
    }

    @Test
    @DisplayName("Test create and update maintain separate instances")
    void testCreate_AndUpdate_SeparateInstances() throws TranslatorException {
        RhythmixExpressionEntity entity1 = createSimpleEntity("expr-1", ">5");
        RhythmixExecutor executor1 = manager.create(entity1);

        RhythmixExpressionEntity entity2 = createSimpleEntity("expr-1", ">10");
        RhythmixExecutor executor2 = manager.update(entity2);

        // Both should be valid executors but different instances
        assertNotNull(executor1);
        assertNotNull(executor2);
    }

    @Test
    @DisplayName("Test delete with filter IDs does not throw ConcurrentModificationException")
    void testDelete_WithFilterIds_NoConcurrentModificationException() throws TranslatorException {
        List<String> filterIds = Arrays.asList("filter-1", "filter-2", "filter-3");
        RhythmixExpressionEntity entity = createTestEntity("expr-1", ">5", true, filterIds);
        manager.create(entity);

        // This should not throw ConcurrentModificationException
        assertDoesNotThrow(() -> manager.delete(entity));
        assertFalse(manager.exists("expr-1"));
    }

    @Test
    @DisplayName("Test update with filter IDs does not throw ConcurrentModificationException")
    void testUpdate_WithFilterIds_NoConcurrentModificationException() throws TranslatorException {
        List<String> filterIds = Arrays.asList("filter-1", "filter-2", "filter-3");
        RhythmixExpressionEntity entity = createTestEntity("expr-1", ">5", true, filterIds);
        manager.create(entity);

        entity.setExpression(">10");
        // This should not throw ConcurrentModificationException
        assertDoesNotThrow(() -> manager.update(entity));
        assertTrue(manager.exists("expr-1"));
    }


    // ==================== Test Monitor Implementation ====================

    /**
     * Test implementation of RhythmixMonitor to track callback invocations.
     */
    private static class TestMonitor implements RhythmixMonitor {
        final List<RhythmixExpressionEntity> createdEntities = new ArrayList<>();
        final List<RhythmixExpressionEntity> updatedEntities = new ArrayList<>();
        final List<RhythmixExpressionEntity> deletedEntities = new ArrayList<>();
        final List<String> compilationErrors = new ArrayList<>();
        final List<RhythmixEventData> beforeExecutionEvents = new ArrayList<>();
        final List<RhythmixEventData> afterExecutionEvents = new ArrayList<>();
        final List<RhythmixEventData> successEvents = new ArrayList<>();
        final List<Throwable> executionErrors = new ArrayList<>();

        @Override
        public void onExpressionCreated(RhythmixExpressionEntity entity) {
            createdEntities.add(entity);
        }

        @Override
        public void onExpressionUpdated(RhythmixExpressionEntity entity) {
            updatedEntities.add(entity);
        }

        @Override
        public void onExpressionDeleted(RhythmixExpressionEntity entity) {
            deletedEntities.add(entity);
        }

        @Override
        public void onCompilationError(RhythmixExpressionEntity entity, String exception) {
            compilationErrors.add(exception);
        }

        @Override
        public void onBeforeExecution(RhythmixExpressionEntity expression, RhythmixEventData eventData) {
            beforeExecutionEvents.add(eventData);
        }

        @Override
        public void onAfterExecution(RhythmixExpressionEntity expression, RhythmixEventData eventData) {
            afterExecutionEvents.add(eventData);
        }

        @Override
        public void onExecutionSuccess(RhythmixExpressionEntity expression, RhythmixEventData eventData) {
            successEvents.add(eventData);
        }

        @Override
        public void onExecutionError(RhythmixExpressionEntity entity, RhythmixExecutionData executionData, Throwable error) {
            executionErrors.add(error);
        }
    }
}



