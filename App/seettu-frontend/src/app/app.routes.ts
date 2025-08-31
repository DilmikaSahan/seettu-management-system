import { Routes } from '@angular/router';
import { Home } from './modules/home/home';
import { LoginComponent } from './modules/auth/login.component';
import { RegisterComponent } from './modules/auth/register.component/register.component';
import { AboutComponent } from './modules/about/about.component';
import { Contact } from './modules/contact/contact';
import { ProviderDashboardComponent } from './modules/provider/provider-dashboard.component';
import { CreatePackageComponent } from './modules/provider/create-package.component';
import { CreateGroupComponent } from './modules/provider/create-group.component';
import { GroupDetailsComponent } from './modules/provider/group-details.component';
import { AddSubscriberComponent } from './modules/provider/add-subscriber.component';
import { SubscriberDashboardComponent } from './modules/subscriber/subscriber-dashboard.component';
import { SubscriberGroupDetailsComponent } from './modules/subscriber/subscriber-group-details.component';
import { RoleGuard } from './guards/role.guard';

export const routes: Routes = [
  { path: '', component: Home },
  { path: 'home', component: Home },
  { path: 'about', component: AboutComponent },
  { path: 'contact', component: Contact },
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },
  
  // Provider routes - protected by role guard
  { 
    path: 'provider/dashboard', 
    component: ProviderDashboardComponent,
    canActivate: [RoleGuard],
    data: { expectedRole: 'PROVIDER' }
  },
  { 
    path: 'provider/packages/create', 
    component: CreatePackageComponent,
    canActivate: [RoleGuard],
    data: { expectedRole: 'PROVIDER' }
  },
  { 
    path: 'provider/groups/create', 
    component: CreateGroupComponent,
    canActivate: [RoleGuard],
    data: { expectedRole: 'PROVIDER' }
  },
  { 
    path: 'provider/groups/:id', 
    component: GroupDetailsComponent,
    canActivate: [RoleGuard],
    data: { expectedRole: 'PROVIDER' }
  },
  { 
    path: 'provider/subscribers/add', 
    component: AddSubscriberComponent,
    canActivate: [RoleGuard],
    data: { expectedRole: 'PROVIDER' }
  },
  
  // Subscriber routes - protected by role guard
  { 
    path: 'subscriber/dashboard', 
    component: SubscriberDashboardComponent,
    canActivate: [RoleGuard],
    data: { expectedRole: 'SUBSCRIBER' }
  },
  { 
    path: 'subscriber/groups/:id', 
    component: SubscriberGroupDetailsComponent,
    canActivate: [RoleGuard],
    data: { expectedRole: 'SUBSCRIBER' }
  },
  
  // Redirect unknown routes to home
  { path: '**', redirectTo: '' }
];
