import { Routes } from '@angular/router';

const routes: Routes = [
  {
    path: 'employee',
    data: { pageTitle: 'factoryApp.employee.home.title' },
    loadChildren: () => import('./employee/employee.routes'),
  },
  /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
];

export default routes;
