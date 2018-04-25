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

package org.activiti.runtime.api.model;

import java.util.Date;
import java.util.List;

import org.activiti.runtime.api.model.builder.CompleteTaskPayload;

public interface Task {

    enum TaskStatus {
        CREATED,
        ASSIGNED,
        SUSPENDED
    }

    String getId();

    String getOwner();

    String getAssignee();

    String getName();

    String getDescription();

    Date getCreatedDate();

    Date getClaimedDate();

    Date getDueDate();

    int getPriority();

    String getProcessDefinitionId();

    String getProcessInstanceId();

    String getParentTaskId();

    TaskStatus getStatus();

    <T> void variable(String name,
                      T value);

    <T> void localVariable(String name,
                       T value);

    List<VariableInstance> variables();

    List<VariableInstance> localVariables();

    void complete();

    CompleteTaskPayload completeWith();

    void claim(String username);

    void release();
}