package com.df.rhythmix.exception;

/**
 * @author MFine
 * @version 1.0
 * @date 2021/7/5 0:31
 **/
public class TypeInferException extends RhythmixException {



    public TypeInferException(String msg) {
        super(msg);
    }

    public TypeInferException(CharSequence template, Object... params) {
        super(template, params);
    }



    @Override
    public String getExceptionType() {
        return "type";
    }
}