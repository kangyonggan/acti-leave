package com.kangyonggan.leave;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngineConfiguration;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.DeploymentBuilder;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.repository.ProcessDefinitionQuery;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskQuery;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.zip.ZipInputStream;

/**
 * @author kangyonggan
 * @date 4/9/18
 */
public class MyTest {

    private ProcessEngine processEngine;

    @Before
    public void before() {
        // 获得一个流程引擎配置对象
        ProcessEngineConfiguration conf = ProcessEngineConfiguration.createProcessEngineConfigurationFromResource("activiti-context.xml");
        // 使用配置对象创建一个流程引擎对象，并且在创建过程中可以自动建表
        processEngine = conf.buildProcessEngine();
    }

    /**
     * demo
     */
    @Test
    public void demo() {
        System.out.println(processEngine.getName());
        System.out.println("系统正常运行");
    }

    /**
     * 部署一个流程图。先使用idea的avtiBPM插件画一个流程图，再调用api接口部署流程图。
     * <p>
     * 向ACT_RE_DEPLOYMENT插入一条记录
     * 向ACT_RE_PROCDEF插入一条记录
     */
    @Test
    public void deploy() {
        // 创建一个部署构建器对象，用于加载流程定义文件(bpmn文件和png文件)
        DeploymentBuilder deploymentBuilder = processEngine.getRepositoryService().createDeployment();
//        deploymentBuilder.addClasspathResource("leave.bpmn");
//        deploymentBuilder.addClasspathResource("leave.png");
        ZipInputStream zipInputStream = new ZipInputStream(this.getClass()
                .getClassLoader().getResourceAsStream("leave.zip"));
        deploymentBuilder.addZipInputStream(zipInputStream);

        // 部署，并返回一个部署对象
        Deployment deployment = deploymentBuilder.deploy();
        System.out.println(deployment.getId());
        System.out.println("流程图部署成功");
    }

    /**
     * 查询流程定义
     */
    @Test
    public void queryList() {
        // 流程定义查询对象，用于查询流程定义表（act_re_procdef）
        ProcessDefinitionQuery query = processEngine.getRepositoryService().createProcessDefinitionQuery();

        // 根据流程定义的key（ACT_RE_PROCDEF.KEY）来过滤, 绝对匹配
        query.processDefinitionKey("myProcess_1");

        // 添加排序条件
        query.orderByProcessDefinitionVersion().desc();

        // 分页查询, 从第startNum开始查，查询limit条，startNum从0开始
        query.listPage(0, 10);

        // 以下查询的是所有的流程定义
        List<ProcessDefinition> list = query.list();
        for (ProcessDefinition pd : list) {
            System.out.println(pd.getId() + "    " + pd.getName() + "    " + pd.getVersion());
        }
        System.out.println("流程图查询成功");
    }

    /**
     * 启动流程实例
     * 会在ACT_RU_EXECUTION表中插入一条数据
     */
    @Test
    public void start() {
        // 流程定义id
        String processDefinitionId = "leave_process:1:4";
        // 根据请假流程定义来具体地请一次假，即启动流程实例
        ProcessInstance processInstance = processEngine.getRuntimeService().startProcessInstanceById(processDefinitionId);
        System.out.println(processInstance.getId());
        System.out.println("启动一个流程实例（一次请假）");
    }

    /**
     * 查询任务
     */
    @Test
    public void queryTask() {
        // 任务查询对象，操作的是任务表(act_ru_task)
        TaskQuery query = processEngine.getTaskService().createTaskQuery();

        // 根据任务的办理人过滤
        query.taskAssignee("ADMIN");
        List<Task> list = query.list();
        for (Task task : list) {
            System.out.println(task.getId() + "\t" + task.getName() + "\t" + task.getAssignee());
        }
        System.out.println("查看ADMIN的所有任务");
    }

    /**
     * 办理任务
     */
    @Test
    public void complete() {
        // 任务的id
        String taskId = "5002";
        processEngine.getTaskService().complete(taskId);
        System.out.println("办理任务5002");
    }

}
