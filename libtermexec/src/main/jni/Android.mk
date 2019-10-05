
LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)
LOCAL_SRC_FILES:= process.cpp

LOCAL_LDLIBS :=  -lc -llog

LOCAL_MODULE:= exec

include $(BUILD_SHARED_LIBRARY)