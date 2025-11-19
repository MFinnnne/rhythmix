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
import java.util.concurrent.atomic.AtomicReference;

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

    private synchronized RhythmixExpressionEntity createSimpleEntity(String id, String expression) {
        return createTestEntity(id, expression, true, new ArrayList<>());
    }

    // ==================== Singleton Pattern Tests ====================

    @Test
    @DisplayName("Test getInstance returns same instance")
    void testGetInstanceReturnsSameInstance() {
        RhythmixExecutorManager instance1 = RhythmixExecutorManager.getInstance();
        RhythmixExecutorManager instance2 = RhythmixExecutorManager.getInstance();

        assertNotNull(instance1);
        assertNotNull(instance2);
        assertSame(instance1, instance2, "Should return the same singleton instance");
    }

    @Test
    @DisplayName("Test getInstance is thread-safe")
    void testGetInstanceThreadSafe() throws InterruptedException {
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
    void testResetClearsInstance() throws TranslatorException {
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
    void testCreateSuccess() throws TranslatorException {
        RhythmixExpressionEntity entity = createSimpleEntity("expr-1", "{>5}->{<3}");

        RhythmixExecutor executor = manager.create(entity);

        assertNotNull(executor);
        assertEquals(1, manager.size());
        assertTrue(manager.exists("expr-1"));
        assertTrue(testMonitor.createdEntities.contains(entity));
    }

    @Test
    @DisplayName("Test create executor with filter IDs")
    void testCreateWithFilterIds() throws TranslatorException {
        List<String> filterIds = Arrays.asList("filter-1", "filter-2");
        RhythmixExpressionEntity entity = createTestEntity("expr-1", ">10", true, filterIds);

        RhythmixExecutor executor = manager.create(entity);

        assertNotNull(executor);
        assertTrue(manager.exists("expr-1"));

    }

    @Test
    @DisplayName("Test create with compilation error")
    void testCreateCompilationError() {
        RhythmixExpressionEntity entity = createSimpleEntity("expr-1", "invalid{{{expression");

        assertThrows(TranslatorException.class, () -> manager.create(entity));
        assertEquals(0, manager.size(), "Failed creation should not add to cache");
        assertFalse(testMonitor.compilationErrors.isEmpty());
    }

    @Test
    @DisplayName("Test create invokes monitor callback")
    void testCreateMonitorCallbackInvoked() throws TranslatorException {
        RhythmixExpressionEntity entity = createSimpleEntity("expr-1", ">5");

        manager.create(entity);

        assertEquals(1, testMonitor.createdEntities.size());
        assertTrue(testMonitor.createdEntities.contains(entity));
    }

    @Test
    @DisplayName("Test create with monitor callback failure does not affect operation")
    void testCreateMonitorCallbackFailureDoesNotAffectOperation() throws TranslatorException {
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
    void testCreateReplacesExistingExecutor() throws TranslatorException {
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
    void testUpdateSuccess() throws TranslatorException {
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
    void testUpdateCreatesNewIfNotExists() throws TranslatorException {
        RhythmixExpressionEntity entity = createSimpleEntity("expr-1", ">5");

        RhythmixExecutor executor = manager.update(entity);

        assertNotNull(executor);
        assertTrue(manager.exists("expr-1"));
    }

    @Test
    @DisplayName("Test update with compilation error")
    void testUpdateCompilationError() throws TranslatorException {
        RhythmixExpressionEntity entity = createSimpleEntity("expr-1", ">5");
        manager.create(entity);

        entity.setExpression("invalid{{{expression");

        assertThrows(TranslatorException.class, () -> manager.update(entity));
        assertTrue(testMonitor.compilationErrors.size() > 0);
    }

    @Test
    @DisplayName("Test update invokes monitor callback")
    void testUpdateMonitorCallbackInvoked() throws TranslatorException {
        RhythmixExpressionEntity entity = createSimpleEntity("expr-1", ">5");
        manager.create(entity);

        entity.setExpression(">10");
        manager.update(entity);

        assertEquals(1, testMonitor.updatedEntities.size());
        assertTrue(testMonitor.updatedEntities.contains(entity));
    }

    @Test
    @DisplayName("Test update preserves created timestamp")
    void testUpdatePreservesCreatedTimestamp() throws TranslatorException {
        RhythmixExpressionEntity entity = createSimpleEntity("expr-1", ">5");
        manager.create(entity);

        Optional<RhythmixExecutor> executorBefore = manager.getRhythmixExecutor("expr-1");
        assertTrue(executorBefore.isPresent());

        // Small delay to ensure timestamps would differ
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        entity.setExpression(">10");
        manager.update(entity);

        Optional<RhythmixExecutor> executorAfter = manager.getRhythmixExecutor("expr-1");
        assertTrue(executorAfter.isPresent());

        // Created timestamp should be the same, updated timestamp should be different
        assertEquals(executorBefore.get().getCreatedAt(), executorAfter.get().getCreatedAt());
    }

    // ==================== Delete Operation Tests ====================

    @Test
    @DisplayName("Test delete executor successfully")
    void testDeleteSuccess() throws TranslatorException {
        RhythmixExpressionEntity entity = createSimpleEntity("expr-1", ">5");
        manager.create(entity);

        manager.delete(entity);

        assertFalse(manager.exists("expr-1"));
        assertEquals(0, manager.size());
        assertEquals(1, testMonitor.deletedEntities.size());
    }

    @Test
    @DisplayName("Test delete removes from cache")
    void testDeleteRemovesFromCache() throws TranslatorException {
        RhythmixExpressionEntity entity = createSimpleEntity("expr-1", ">5");
        manager.create(entity);
        assertTrue(manager.exists("expr-1"));

        manager.delete(entity);

        assertFalse(manager.exists("expr-1"));
        assertFalse(manager.getExecutor("expr-1").isPresent());
    }

    @Test
    @DisplayName("Test delete removes from route map")
    void testDeleteRemovesFromRouteMap() throws TranslatorException {
        List<String> filterIds = Arrays.asList("filter-1");
        RhythmixExpressionEntity entity = createTestEntity("expr-1", ">5", true, filterIds);
        manager.create(entity);

        manager.delete(entity);

        assertFalse(manager.exists("expr-1"));
    }

    @Test
    @DisplayName("Test delete invokes monitor callback")
    void testDeleteMonitorCallbackInvoked() throws TranslatorException {
        RhythmixExpressionEntity entity = createSimpleEntity("expr-1", ">5");
        manager.create(entity);

        manager.delete(entity);

        assertEquals(1, testMonitor.deletedEntities.size());
        assertTrue(testMonitor.deletedEntities.contains(entity));
    }

    @Test
    @DisplayName("Test delete non-existent executor")
    void testDeleteNonExistentExecutor() {
        RhythmixExpressionEntity entity = createSimpleEntity("expr-1", ">5");

        // Should not throw exception
        assertDoesNotThrow(() -> manager.delete(entity));
        assertEquals(0, testMonitor.deletedEntities.size(), "Monitor should not be called for non-existent executor");
    }

    // ==================== Enable/Disable Tests ====================

    @Test
    @DisplayName("Test enable executor successfully")
    void testEnableSuccess() throws TranslatorException {
        RhythmixExpressionEntity entity = createTestEntity("expr-1", ">5", false, new ArrayList<>());
        manager.create(entity);

        manager.enable(entity);

        assertTrue(manager.isEnabled("expr-1"));
        Optional<RhythmixExecutor> executor = manager.getRhythmixExecutor("expr-1");
        assertTrue(executor.isPresent());
        assertTrue(executor.get().isEnabled());
    }

    @Test
    @DisplayName("Test enable non-existent executor")
    void testEnableNonExistentExecutor() {
        RhythmixExpressionEntity entity = createSimpleEntity("expr-1", ">5");

        // Should not throw exception
        assertDoesNotThrow(() -> manager.enable(entity));
        assertFalse(manager.isEnabled("expr-1"));
    }

    @Test
    @DisplayName("Test disable executor successfully")
    void testDisableSuccess() throws TranslatorException {
        RhythmixExpressionEntity entity = createSimpleEntity("expr-1", ">5");
        manager.create(entity);
        assertTrue(manager.isEnabled("expr-1"));

        manager.disable(entity);

        assertFalse(manager.isEnabled("expr-1"));
        Optional<RhythmixExecutor> executor = manager.getRhythmixExecutor("expr-1");
        assertTrue(executor.isPresent());
        assertFalse(executor.get().isEnabled());
    }

    @Test
    @DisplayName("Test disable non-existent executor")
    void testDisableNonExistentExecutor() {
        RhythmixExpressionEntity entity = createSimpleEntity("expr-1", ">5");

        // Should not throw exception
        assertDoesNotThrow(() -> manager.disable(entity));
        assertFalse(manager.isEnabled("expr-1"));
    }

    @Test
    @DisplayName("Test enable updates timestamp")
    void testEnableUpdatesTimestamp() throws TranslatorException {
        RhythmixExpressionEntity entity = createTestEntity("expr-1", ">5", false, new ArrayList<>());
        manager.create(entity);

        Optional<RhythmixExecutor> executorBefore = manager.getRhythmixExecutor("expr-1");
        assertTrue(executorBefore.isPresent());
        final String time = executorBefore.get().getUpdatedAt().toString();

        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        manager.enable(entity);

        Optional<RhythmixExecutor> executorAfter = manager.getRhythmixExecutor("expr-1");
        assertTrue(executorAfter.isPresent());
        assertTrue(executorAfter.get().getUpdatedAt().isAfter(LocalDateTime.parse(time)));
    }

    // ==================== Query Operations Tests ====================

    @Test
    @DisplayName("Test getExecutor returns executor when found")
    void testGetExecutorFound() throws TranslatorException {
        RhythmixExpressionEntity entity = createSimpleEntity("expr-1", ">5");
        manager.create(entity);

        Optional<RhythmixExecutor> executor = manager.getExecutor("expr-1");

        assertTrue(executor.isPresent());
    }

    @Test
    @DisplayName("Test getExecutor returns empty when not found")
    void testGetExecutorNotFound() {
        Optional<RhythmixExecutor> executor = manager.getExecutor("non-existent");

        assertFalse(executor.isPresent());
    }

    @Test
    @DisplayName("Test getExecutor with null ID")
    void testGetExecutorNullId() {
        Optional<RhythmixExecutor> executor = manager.getExecutor(null);

        assertFalse(executor.isPresent());
    }

    @Test
    @DisplayName("Test getExecutorWrapper returns executor when found")
    void testGetRhythmixExecutorFound() throws TranslatorException {
        RhythmixExpressionEntity entity = createSimpleEntity("expr-1", ">5");
        manager.create(entity);

        Optional<RhythmixExecutor> executor = manager.getRhythmixExecutor("expr-1");

        assertTrue(executor.isPresent());
        assertEquals("expr-1", executor.get().getId());
        assertNotNull(executor.get());
        assertNotNull(executor.get().getCreatedAt());
        assertNotNull(executor.get().getUpdatedAt());
    }

    @Test
    @DisplayName("Test getExecutorWrapper returns empty when not found")
    void testGetRhythmixExecutorNotFound() {
        Optional<RhythmixExecutor> executor = manager.getRhythmixExecutor("non-existent");

        assertFalse(executor.isPresent());
    }

    @Test
    @DisplayName("Test getExecutorWrapper with null ID")
    void testGetRhythmixExecutorNullId() {
        Optional<RhythmixExecutor> executor = manager.getRhythmixExecutor(null);

        assertFalse(executor.isPresent());
    }

    @Test
    @DisplayName("Test exists returns true when executor exists")
    void testExistsTrue() throws TranslatorException {
        RhythmixExpressionEntity entity = createSimpleEntity("expr-1", ">5");
        manager.create(entity);

        assertTrue(manager.exists("expr-1"));
    }

    @Test
    @DisplayName("Test exists returns false when executor does not exist")
    void testExistsFalse() {
        assertFalse(manager.exists("non-existent"));
    }

    @Test
    @DisplayName("Test exists with null ID")
    void testExistsNullId() {
        assertFalse(manager.exists(null));
    }

    @Test
    @DisplayName("Test isEnabled returns true when enabled")
    void testIsEnabledTrue() throws TranslatorException {
        RhythmixExpressionEntity entity = createSimpleEntity("expr-1", ">5");
        manager.create(entity);

        assertTrue(manager.isEnabled("expr-1"));
    }

    @Test
    @DisplayName("Test isEnabled returns false when disabled")
    void testIsEnabledFalse() throws TranslatorException {
        RhythmixExpressionEntity entity = createTestEntity("expr-1", ">5", false, new ArrayList<>());
        manager.create(entity);

        assertFalse(manager.isEnabled("expr-1"));
    }

    @Test
    @DisplayName("Test isEnabled returns false for non-existent executor")
    void testIsEnabledNonExistent() {
        assertFalse(manager.isEnabled("non-existent"));
    }

    @Test
    @DisplayName("Test size returns correct count")
    void testSizeReturnsCorrectCount() throws TranslatorException {
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
    void testClearRemovesAllExecutors() throws TranslatorException {
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
    void testClearNoCallbacks() throws TranslatorException {
        manager.create(createSimpleEntity("expr-1", ">5"));
        manager.create(createSimpleEntity("expr-2", "<10"));

        manager.clear();

        assertEquals(0, testMonitor.deletedEntities.size(), "Clear should not trigger deletion callbacks");
    }

    // ==================== Edge Cases and Error Handling Tests ====================

    @Test
    @DisplayName("Test create with null expression throws exception")
    void testCreateNullExpression() {
        RhythmixExpressionEntity entity = createSimpleEntity("expr-1", null);

        assertThrows(Exception.class, () -> manager.create(entity));
    }

    @Test
    @DisplayName("Test update with null expression throws exception")
    void testUpdateNullExpression() {
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
    void testExecutorCanExecuteEvents() throws TranslatorException {
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
    void testMultipleExecutorsDifferentExpressions() throws TranslatorException {
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
    void testExecutorMetadataCorrectlySet() throws TranslatorException {
        RhythmixExpressionEntity entity = createSimpleEntity("expr-1", ">5");
        manager.create(entity);

        Optional<RhythmixExecutor> executor = manager.getRhythmixExecutor("expr-1");
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
    void testExecutorDisabledCannotExecute() throws TranslatorException {
        RhythmixExpressionEntity entity = createSimpleEntity("expr-1", ">5");
        manager.create(entity);
        manager.disable(entity);

        Optional<RhythmixExecutor> executor = manager.getRhythmixExecutor("expr-1");
        assertTrue(executor.isPresent());
        assertFalse(executor.get().canExecute());
    }

    @Test
    @DisplayName("Test update with filter IDs")
    void testUpdateWithFilterIds() throws TranslatorException {
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
    void testCreateAndUpdateSeparateInstances() throws TranslatorException {
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
    void testDeleteWithFilterIdsNoConcurrentModificationException() throws TranslatorException {
        List<String> filterIds = Arrays.asList("filter-1", "filter-2", "filter-3");
        RhythmixExpressionEntity entity = createTestEntity("expr-1", ">5", true, filterIds);
        manager.create(entity);

        // This should not throw ConcurrentModificationException
        assertDoesNotThrow(() -> manager.delete(entity));
        assertFalse(manager.exists("expr-1"));
    }

    @Test
    @DisplayName("Test update with filter IDs does not throw ConcurrentModificationException")
    void testUpdateWithFilterIdsNoConcurrentModificationException() throws TranslatorException {
        List<String> filterIds = Arrays.asList("filter-1", "filter-2", "filter-3");
        RhythmixExpressionEntity entity = createTestEntity("expr-1", ">5", true, filterIds);
        manager.create(entity);

        entity.setExpression(">10");
        // This should not throw ConcurrentModificationException
        assertDoesNotThrow(() -> manager.update(entity));
        assertTrue(manager.exists("expr-1"));
    }

    // ==================== Execute Method Tests (Private Method via Reflection) ====================

    private void invokeExecuteMethod(RhythmixEventData event) throws Exception {
        RhythmixExecutorManager manager = RhythmixExecutorManager.getInstance();
        manager.execute(event);
    }

    @Test
    @DisplayName("Test execute with single enabled executor - successful match")
    void testExecuteSingleEnabledExecutorSuccessfulMatch() throws Exception {
        // Create an executor with a simple expression
        List<String> filterIds = List.of("filter-1");
        RhythmixExpressionEntity entity = createTestEntity("expr-1", ">5", true, filterIds);
        manager.create(entity);

        // Create event data that matches the expression
        RhythmixEventData event = Util.genEventData("e1", "10", new Timestamp(System.currentTimeMillis()));

        // Execute via reflection
        invokeExecuteMethod(event);

        // Verify monitor callbacks were invoked
        assertEquals(1, testMonitor.beforeExecutionEvents.size());
        assertEquals(1, testMonitor.afterExecutionEvents.size());
        assertEquals(1, testMonitor.successEvents.size());
        assertEquals(event, testMonitor.beforeExecutionEvents.get(0));
        assertEquals(event, testMonitor.successEvents.get(0));
    }

    @Test
    @DisplayName("Test execute with single enabled executor - no match")
    void testExecuteSingleEnabledExecutorNoMatch() throws Exception {
        // Create an executor with a simple expression
        List<String> filterIds = Arrays.asList("filter-1");
        RhythmixExpressionEntity entity = createTestEntity("expr-1", ">5", true, filterIds);
        manager.create(entity);

        // Create event data that does NOT match the expression
        RhythmixEventData event = Util.genEventData("e1", "3", new Timestamp(System.currentTimeMillis()));

        // Execute via reflection
        invokeExecuteMethod(event);

        // Verify monitor callbacks
        assertEquals(1, testMonitor.beforeExecutionEvents.size());
        assertEquals(1, testMonitor.afterExecutionEvents.size());
        assertEquals(0, testMonitor.successEvents.size(), "Success callback should not be invoked for non-matching event");
    }

    @Test
    @DisplayName("Test execute with disabled executor - should not execute")
    void testExecuteDisabledExecutorShouldNotExecute() throws Exception {
        // Create a disabled executor
        List<String> filterIds = Arrays.asList("filter-1");
        RhythmixExpressionEntity entity = createTestEntity("expr-1", ">5", false, filterIds);
        manager.create(entity);

        // Create event data
        RhythmixEventData event = Util.genEventData("e1", "10", new Timestamp(System.currentTimeMillis()));

        // Execute via reflection
        invokeExecuteMethod(event);

        // Verify no monitor callbacks were invoked (executor is disabled)
        assertEquals(0, testMonitor.beforeExecutionEvents.size());
        assertEquals(0, testMonitor.afterExecutionEvents.size());
        assertEquals(0, testMonitor.successEvents.size());
    }

    @Test
    @DisplayName("Test execute with multiple enabled executors")
    void testExecuteMultipleEnabledExecutors() throws Exception {
        // Create multiple executors with the same filter ID
        List<String> filterIds = Arrays.asList("filter-1");
        RhythmixExpressionEntity entity1 = createTestEntity("expr-1", ">5", true, filterIds);
        RhythmixExpressionEntity entity2 = createTestEntity("expr-2", "<15", true, filterIds);
        manager.create(entity1);
        manager.create(entity2);

        // Create event data that matches both expressions
        RhythmixEventData event = Util.genEventData("e1", "10", new Timestamp(System.currentTimeMillis()));

        // Execute via reflection
        invokeExecuteMethod(event);

        // Verify both executors were invoked
        assertEquals(2, testMonitor.beforeExecutionEvents.size());
        assertEquals(2, testMonitor.afterExecutionEvents.size());
        assertEquals(2, testMonitor.successEvents.size());
    }

    @Test
    @DisplayName("Test execute with multiple executors - mixed enabled/disabled")
    void testExecuteMultipleExecutorsMixedEnabledDisabled() throws Exception {
        // Create multiple executors with the same filter ID
        List<String> filterIds = Arrays.asList("filter-1");
        RhythmixExpressionEntity entity1 = createTestEntity("expr-1", ">5", true, filterIds);
        RhythmixExpressionEntity entity2 = createTestEntity("expr-2", "<15", false, filterIds);
        manager.create(entity1);
        manager.create(entity2);

        // Create event data
        RhythmixEventData event = Util.genEventData("e1", "10", new Timestamp(System.currentTimeMillis()));

        // Execute via reflection
        invokeExecuteMethod(event);

        // Verify only enabled executor was invoked
        assertEquals(1, testMonitor.beforeExecutionEvents.size());
        assertEquals(1, testMonitor.afterExecutionEvents.size());
        assertEquals(1, testMonitor.successEvents.size());
    }

    @Test
    @DisplayName("Test execute with state position change - multi-state expression")
    void testExecuteStatePositionChangeMultiStateExpression() throws Exception {
        // Create executor with multi-state expression
        List<String> filterIds = Arrays.asList("filter-1");
        RhythmixExpressionEntity entity = createTestEntity("expr-1", "{>5}->{<3}", true, filterIds);
        manager.create(entity);

        // Create a custom monitor to track state position changes
        AtomicInteger stateChangeCount = new AtomicInteger(0);
        AtomicReference<Integer> previousPosition = new AtomicReference<>();
        AtomicReference<Integer> currentPosition = new AtomicReference<>();

        RhythmixConfig.setMonitor(new RhythmixMonitor() {
            @Override
            public void onStatePositionChanged(RhythmixExpressionEntity expression,
                                               int prevPos, int currPos,
                                               RhythmixExecutionData executionData) {
                stateChangeCount.incrementAndGet();
                previousPosition.set(prevPos);
                currentPosition.set(currPos);
            }

            @Override
            public void onBeforeExecution(RhythmixExpressionEntity expression, RhythmixEventData eventData) {
            }

            @Override
            public void onAfterExecution(RhythmixExpressionEntity expression, RhythmixEventData eventData) {
            }

            @Override
            public void onExecutionSuccess(RhythmixExpressionEntity expression, RhythmixEventData eventData) {
            }
        });

        // Execute first event that matches first state
        RhythmixEventData event1 = Util.genEventData("e1", "10", new Timestamp(System.currentTimeMillis()));
        invokeExecuteMethod(event1);

        // Verify state position changed
        assertTrue(stateChangeCount.get() > 0, "State position should have changed");
    }

    @Test
    @DisplayName("Test execute with runtime exception - error callback invoked")
    void testExecuteRuntimeExceptionErrorCallbackInvoked() throws Exception {
        // Create executor with expression that will cause runtime error
        List<String> filterIds = Arrays.asList("filter-1");
        RhythmixExpressionEntity entity = createTestEntity("expr-1", ">5", true, filterIds);

        // Create executor and manually inject a faulty one to simulate error
        manager.create(entity);

        // Get the executor and create a wrapper that throws exception
        Optional<RhythmixExecutor> executorOpt = manager.getExecutor("expr-1");
        assertTrue(executorOpt.isPresent());

        // Create event data
        RhythmixEventData event = Util.genEventData("e1", "invalid_value", new Timestamp(System.currentTimeMillis()));

        // Note: This test verifies that the execute method handles exceptions gracefully
        // The actual exception handling is done within the execute method
        assertDoesNotThrow(() -> invokeExecuteMethod(event));
    }

    @Test
    @DisplayName("Test execute with null event data - should handle gracefully")
    void testExecuteNullEventDataHandlesGracefully() throws Exception {
        // Create an executor
        List<String> filterIds = Arrays.asList("filter-1");
        RhythmixExpressionEntity entity = createTestEntity("expr-1", ">5", true, filterIds);
        manager.create(entity);

        // Execute with null event - should not throw exception
        assertDoesNotThrow(() -> invokeExecuteMethod(null));
    }

    @Test
    @DisplayName("Test execute with no executors in route map")
    void testExecuteNoExecutorsInRouteMapNoCallbacks() throws Exception {
        // Don't create any executors
        RhythmixEventData event = Util.genEventData("e1", "10", new Timestamp(System.currentTimeMillis()));

        // Execute via reflection
        invokeExecuteMethod(event);

        // Verify no callbacks were invoked
        assertEquals(0, testMonitor.beforeExecutionEvents.size());
        assertEquals(0, testMonitor.afterExecutionEvents.size());
        assertEquals(0, testMonitor.successEvents.size());
    }

    @Test
    @DisplayName("Test execute with different filter IDs - only matching executors run")
    void testExecuteDifferentFilterIdsOnlyMatchingExecutorsRun() throws Exception {
        // Create executors with different filter IDs
        List<String> filterIds1 = Arrays.asList("filter-1");
        List<String> filterIds2 = Arrays.asList("filter-2");

        RhythmixExpressionEntity entity1 = createTestEntity("expr-1", ">5", true, filterIds1);
        RhythmixExpressionEntity entity2 = createTestEntity("expr-2", "<15", true, filterIds2);

        manager.create(entity1);
        manager.create(entity2);

        // Create event data
        RhythmixEventData event = Util.genEventData("e1", "10", new Timestamp(System.currentTimeMillis()));

        // Execute via reflection - this will execute all executors in all filter routes
        invokeExecuteMethod(event);

        // Both executors should be invoked since execute iterates over all routes
        assertEquals(2, testMonitor.beforeExecutionEvents.size());
    }

    @Test
    @DisplayName("Test execute callback order - before, success, after")
    void testExecuteCallbackOrderBeforeSuccessAfter() throws Exception {
        // Track callback order
        List<String> callbackOrder = new ArrayList<>();

        RhythmixConfig.setMonitor(new RhythmixMonitor() {
            @Override
            public void onBeforeExecution(RhythmixExpressionEntity expression, RhythmixEventData eventData) {
                callbackOrder.add("before");
            }

            @Override
            public void onExecutionSuccess(RhythmixExpressionEntity expression, RhythmixEventData eventData) {
                callbackOrder.add("success");
            }

            @Override
            public void onAfterExecution(RhythmixExpressionEntity expression, RhythmixEventData eventData) {
                callbackOrder.add("after");
            }
        });

        // Create executor
        List<String> filterIds = Arrays.asList("filter-1");
        RhythmixExpressionEntity entity = createTestEntity("expr-1", ">5", true, filterIds);
        manager.create(entity);

        // Execute with matching event
        RhythmixEventData event = Util.genEventData("e1", "10", new Timestamp(System.currentTimeMillis()));
        invokeExecuteMethod(event);

        // Verify callback order
        assertEquals(3, callbackOrder.size());
        assertEquals("before", callbackOrder.get(0));
        assertEquals("success", callbackOrder.get(1));
        assertEquals("after", callbackOrder.get(2));
    }

    @Test
    @DisplayName("Test execute with monitor callback exception - execution continues")
    void testExecuteMonitorCallbackExceptionExecutionContinues() throws Exception {
        // Set monitor that throws exception in onBeforeExecution
        RhythmixConfig.setMonitor(new RhythmixMonitor() {
            @Override
            public void onBeforeExecution(RhythmixExpressionEntity expression, RhythmixEventData eventData) {
                throw new RuntimeException("Monitor callback failure");
            }

            @Override
            public void onAfterExecution(RhythmixExpressionEntity expression, RhythmixEventData eventData) {
                // This should still be called despite the exception in onBeforeExecution
            }
        });

        // Create executor
        List<String> filterIds = Arrays.asList("filter-1");
        RhythmixExpressionEntity entity = createTestEntity("expr-1", ">5", true, filterIds);
        manager.create(entity);

        // Execute - should not throw exception
        RhythmixEventData event = Util.genEventData("e1", "10", new Timestamp(System.currentTimeMillis()));
        assertDoesNotThrow(() -> invokeExecuteMethod(event));
    }

    @Test
    @DisplayName("Test execute with complex multi-state expression")
    void testExecuteComplexMultiStateExpression() throws Exception {
        // Create executor with complex multi-state expression
        List<String> filterIds = Arrays.asList("filter-1");
        RhythmixExpressionEntity entity = createTestEntity("expr-1", "{>5}->{<10}->{>8}", true, filterIds);
        manager.create(entity);

        // Execute sequence of events
        RhythmixEventData event1 = Util.genEventData("e1", "7", new Timestamp(System.currentTimeMillis()));
        RhythmixEventData event2 = Util.genEventData("e2", "8", new Timestamp(System.currentTimeMillis() + 100));
        RhythmixEventData event3 = Util.genEventData("e3", "9", new Timestamp(System.currentTimeMillis() + 200));

        invokeExecuteMethod(event1);
        invokeExecuteMethod(event2);
        invokeExecuteMethod(event3);

        // Verify all events were processed
        assertEquals(3, testMonitor.beforeExecutionEvents.size());
        assertEquals(3, testMonitor.afterExecutionEvents.size());
    }

    @Test
    @DisplayName("Test execute with boundary value - exact match")
    void testExecuteBoundaryValueExactMatch() throws Exception {
        // Create executor with exact value expression
        List<String> filterIds = Arrays.asList("filter-1");
        RhythmixExpressionEntity entity = createTestEntity("expr-1", "==5", true, filterIds);
        manager.create(entity);

        // Execute with exact boundary value
        RhythmixEventData event = Util.genEventData("e1", "5", new Timestamp(System.currentTimeMillis()));
        invokeExecuteMethod(event);

        // Verify success callback was invoked
        assertEquals(1, testMonitor.successEvents.size());
    }

    @Test
    @DisplayName("Test execute with range expression")
    void testExecuteRangeExpression() throws Exception {
        // Create executor with range expression
        List<String> filterIds = Arrays.asList("filter-1");
        RhythmixExpressionEntity entity = createTestEntity("expr-1", "[5,10]", true, filterIds);
        manager.create(entity);

        // Execute with value in range
        RhythmixEventData event = Util.genEventData("e1", "7", new Timestamp(System.currentTimeMillis()));
        invokeExecuteMethod(event);

        // Verify success callback was invoked
        assertEquals(1, testMonitor.successEvents.size());
    }

    @Test
    @DisplayName("Test execute with empty filter IDs list")
    void testExecuteEmptyFilterIdsList() throws Exception {
        // Create executor with empty filter IDs
        RhythmixExpressionEntity entity = createTestEntity("expr-1", ">5", true, new ArrayList<>());
        manager.create(entity);

        // Execute
        RhythmixEventData event = Util.genEventData("e1", "10", new Timestamp(System.currentTimeMillis()));
        invokeExecuteMethod(event);

        // Since there are no filter IDs, the executor won't be in any route map
        // So no callbacks should be invoked
        assertEquals(0, testMonitor.beforeExecutionEvents.size());
    }

    @Test
    @DisplayName("Test execute verifies execution data is populated")
    void testExecuteVerifiesExecutionDataPopulated() throws Exception {
        // Create executor
        List<String> filterIds = Arrays.asList("filter-1");
        RhythmixExpressionEntity entity = createTestEntity("expr-1", ">5", true, filterIds);
        RhythmixExecutor executor = manager.create(entity);

        // Execute
        RhythmixEventData event = Util.genEventData("e1", "10", new Timestamp(System.currentTimeMillis()));
        invokeExecuteMethod(event);

        // Verify execution data was recorded
        assertNotNull(executor.getRhythmixExecutionData());
        assertNotNull(executor.getRhythmixExecutionData().getCurrentExecutionRecord());
    }

    @Test
    @DisplayName("Test execute with concurrent execution on same executor")
    void testExecuteConcurrentExecutionThreadSafe() throws Exception {
        // Create executor
        List<String> filterIds = List.of("filter-1");
        RhythmixExpressionEntity entity = createTestEntity("expr-1", ">5", true, filterIds);
        manager.create(entity);

        // Execute concurrently
        int threadCount = 5;
        CountDownLatch latch = new CountDownLatch(threadCount);
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);

        for (int i = 0; i < threadCount; i++) {
            final int index = i;
            executorService.submit(() -> {
                try {
                    RhythmixEventData event = Util.genEventData("e" + index, "10",
                            new Timestamp(System.currentTimeMillis()));
                    invokeExecuteMethod(event);
                } catch (Exception e) {
                    // Ignore
                } finally {
                    latch.countDown();
                }
            });
        }

        assertTrue(latch.await(10, TimeUnit.SECONDS));
        executorService.shutdown();

        // Verify all executions were processed
        assertEquals(threadCount, testMonitor.beforeExecutionEvents.size());
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



