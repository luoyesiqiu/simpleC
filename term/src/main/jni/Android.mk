
LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)
LOCAL_SRC_FILES:= common.cpp fileCompat.cpp termExec.cpp

LOCAL_LDLIBS :=  -lc -llog

LOCAL_MODULE:= term

include $(BUILD_SHARED_LIBRARY)