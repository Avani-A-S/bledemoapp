# Android Makefile

LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)

PATH_SEP := /

#traverse all the directory and subdirectory
define walk
  $(wildcard $(1)) $(foreach e, $(wildcard $(1)$(PATH_SEP)*), $(call walk, $(e)))
endef

SRC_LIST :=
INCLUDE_LIST :=

################################
# prepare shared lib

LOCAL_MODULE := rust_android

# JNI interface files
INCLUDE_LIST += $(LOCAL_PATH)
SRC_LIST += $(wildcard $(LOCAL_PATH)/*.cpp)

$(info LOCAL_PATH:$(LOCAL_PATH))
$(info SRC_LIST:$(SRC_LIST))
$(info INCLUDE_LIST:$(INCLUDE_LIST))

LOCAL_C_INCLUDES := $(INCLUDE_LIST)
LOCAL_SRC_FILES := $(SRC_LIST:$(LOCAL_PATH)/%=%)

LOCAL_CPPFLAGS += -fblocks -DUNICODE 
TARGET_PLATFORM := android-27
LOCAL_DISABLE_FATAL_LINKER_WARNINGS := false
LOCAL_CPP_FEATURES := exceptions 

include $(BUILD_SHARED_LIBRARY)

################################
