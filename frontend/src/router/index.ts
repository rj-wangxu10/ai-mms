import { createRouter, createWebHistory } from 'vue-router';
import Layout from '../components/Layout.vue';
import EmployeeDashboard from '../views/employee/Dashboard.vue';
import ManagerDashboard from '../views/manager/Dashboard.vue';
import AdminDashboard from '../views/admin/Dashboard.vue';
import AdminBudget from '../views/admin/Budget.vue';
import AdminTool from '../views/admin/Tool.vue';
import AdminUser from '../views/admin/User.vue';
import AdminImport from '../views/admin/Import.vue';
import AdminApplication from '../views/admin/Application.vue';
import AdminAlert from '../views/admin/Alert.vue';
import ManagerApplication from '../views/manager/Application.vue';
import ManagerMember from '../views/manager/Member.vue';
import EmployeeApplication from '../views/employee/Application.vue';
import EmployeeUsage from '../views/employee/Usage.vue';

const routes = [
  {
    path: '/',
    component: Layout,
    redirect: '/employee',
    children: [
      { path: 'employee', component: EmployeeDashboard, meta: { title: '员工看板', role: 'employee' } },
      { path: 'employee/usage', component: EmployeeUsage, meta: { title: '消费明细', role: 'employee' } },
      { path: 'employee/application', component: EmployeeApplication, meta: { title: '额度申请', role: 'employee' } },
      { path: 'manager', component: ManagerDashboard, meta: { title: '主管看板', role: 'manager' } },
      { path: 'manager/member', component: ManagerMember, meta: { title: '成员排名', role: 'manager' } },
      { path: 'manager/application', component: ManagerApplication, meta: { title: '审批', role: 'manager' } },
      { path: 'admin', component: AdminDashboard, meta: { title: '全局看板', role: 'admin' } },
      { path: 'admin/budget', component: AdminBudget, meta: { title: '预算配置', role: 'admin' } },
      { path: 'admin/tool', component: AdminTool, meta: { title: '工具管理', role: 'admin' } },
      { path: 'admin/user', component: AdminUser, meta: { title: '用户管理', role: 'admin' } },
      { path: 'admin/import', component: AdminImport, meta: { title: '账单导入', role: 'admin' } },
      { path: 'admin/application', component: AdminApplication, meta: { title: '终审', role: 'admin' } },
      { path: 'admin/alert', component: AdminAlert, meta: { title: '预警配置', role: 'admin' } }
    ]
  }
];

const router = createRouter({
  history: createWebHistory(),
  routes
});

export default router;
