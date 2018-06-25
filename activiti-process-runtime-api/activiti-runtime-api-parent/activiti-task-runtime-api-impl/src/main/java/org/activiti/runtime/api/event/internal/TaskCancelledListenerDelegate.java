/*
 * Copyright 2018 Alfresco, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.activiti.runtime.api.event.internal;

import java.util.List;

import org.activiti.engine.delegate.event.ActivitiActivityCancelledEvent;
import org.activiti.engine.delegate.event.ActivitiEvent;
import org.activiti.engine.delegate.event.ActivitiEventListener;
import org.activiti.runtime.api.event.impl.ToTaskCancelledConverter;
import org.activiti.runtime.api.event.listener.TaskRuntimeEventListener;

public class TaskCancelledListenerDelegate implements ActivitiEventListener {

    private final List<TaskRuntimeEventListener> taskRuntimeEventListeners;

    private final ToTaskCancelledConverter toTaskCancelledConverter;

    public TaskCancelledListenerDelegate(List<TaskRuntimeEventListener> taskRuntimeEventListeners,
                                         ToTaskCancelledConverter toTaskCancelledConverter) {
        this.taskRuntimeEventListeners = taskRuntimeEventListeners;
        this.toTaskCancelledConverter = toTaskCancelledConverter;
    }

    @Override
    public void onEvent(ActivitiEvent event) {
        if (event instanceof ActivitiActivityCancelledEvent) {
            toTaskCancelledConverter.from((ActivitiActivityCancelledEvent) event)
                    .ifPresent(convertedEvent -> {
                        for (TaskRuntimeEventListener listener : taskRuntimeEventListeners) {
                            listener.onTaskCancelled(convertedEvent);
                        }
                    });
        }
    }

    @Override
    public boolean isFailOnException() {
        return false;
    }
}
