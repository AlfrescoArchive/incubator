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

package org.activiti.runtime.api;

import java.util.List;

import org.activiti.runtime.api.model.ProcessInstance;
import org.activiti.runtime.api.model.Task;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TaskRuntimeIT {

    @Autowired
    private TaskRuntime taskRuntime;

    @Autowired
    private ProcessRuntime processRuntime;

    @Test
    public void shouldRetrieveTasks() {
        //given
        ProcessInstance firstSimpleProcess = processRuntime.processDefinitionWithKey("SimpleProcess").start();
        ProcessInstance secondSimpleProcess = processRuntime.processDefinitionWithKey("SimpleProcess").start();

        //when
        List<Task> tasks = taskRuntime.tasks(0,
                                             500);

        //then
        assertThat(tasks).isNotNull();
        assertThat(tasks)
                .extracting(Task::getName, Task::getProcessInstanceId)
                .contains(tuple("Perform action",
                                firstSimpleProcess.getId()),
                          tuple("Perform action", secondSimpleProcess.getId()));
    }

    @Test
    public void shouldCompleteTask() {
        //given
        Task task = aTask();

        //when
        task.complete();

        //then
        assertCompleted(task);
    }

    private Task aTask() {
        ProcessInstance processInstance = processRuntime.processDefinitionWithKey("SimpleProcess").start();
        Task currentTask = taskRuntime.tasks(0,
                                             500).stream()
                .filter(task -> task.getProcessInstanceId().equals(processInstance.getId()))
                .findFirst().orElse(null);
        assertThat(currentTask).isNotNull();
        return currentTask;
    }

    private void assertCompleted(Task currentTask) {
        List<Task> tasks = taskRuntime.tasks(0,
                                             500);
        assertThat(tasks).doesNotContain(currentTask);
    }

    @Test
    public void shouldCompleteTaskWithExtraInformation() {
        //given
        Task currentTask = aTask();

        //when
        currentTask
                .completeWith()
                .variable("bookRef", "abc")
                .variable("position", 10)
                .doIt();

        //then
        assertCompleted(currentTask);
    }

    @Test
    public void shouldGetTaskById() {
        //given
        Task task = aTask();

        //when
        Task retrievedTask = taskRuntime.task(task.getId());

        //then
        assertThat(retrievedTask).isEqualTo(task);
    }

    @Test
    public void claimTaskShouldSetAssignee() {
        //given
        Task task = aTask();

        //when
        task.claim("paul");

        //then
        Task retrievedTask = taskRuntime.task(task.getId());
        assertThat(retrievedTask.getAssignee()).isEqualTo("paul");
        assertThat(retrievedTask.getStatus()).isEqualTo(Task.TaskStatus.ASSIGNED);
    }

    @Test
    public void releaseShouldUnSetAssignee() {
        //given
        Task task = aTask();
        task.claim("paul");

        //when
        task.release();

        //then
        Task retrievedTask = taskRuntime.task(task.getId());
        assertThat(retrievedTask.getAssignee()).isNull();
        assertThat(retrievedTask.getStatus()).isEqualTo(Task.TaskStatus.CREATED);
    }
}