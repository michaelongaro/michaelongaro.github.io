import { Routes } from '@angular/router';
import { TripFormComponent } from './trip-form/trip-form';
import { TripListingComponent } from './trip-listing/trip-listing';
import { LoginComponent } from './login/login';

export const routes: Routes = [
  {
    path: 'add-trip',
    component: TripFormComponent,
    data: { mode: 'add' },
  },
  {
    path: 'edit-trip',
    component: TripFormComponent,
    data: { mode: 'edit' },
  },
  { path: 'login', component: LoginComponent },
  { path: '', component: TripListingComponent, pathMatch: 'full' },
];
