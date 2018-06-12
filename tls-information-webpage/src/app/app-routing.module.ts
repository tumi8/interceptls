import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { HomeComponent }   from './home/home.component';
import { FeaturesComponent }   from './features/features.component';
import { DownloadComponent }   from './download/download.component';
import { StatisticsComponent }   from './statistics/statistics.component';
import { AboutComponent }   from './about/about.component';
import { FaqComponent }   from './faq/faq.component';
import { PrivacyComponent }   from './privacy/privacy.component';

const routes: Routes = [
  { path: '', component: HomeComponent },
  { path: 'features', component: FeaturesComponent },
  { path: 'download', component: DownloadComponent },
  { path: 'statistics', component: StatisticsComponent },
  { path: 'about', component: AboutComponent },
  { path: 'faq', component: FaqComponent },
  { path: 'privacy', component: PrivacyComponent },
];

@NgModule({
  imports: [ RouterModule.forRoot(routes) ],
  exports: [ RouterModule ]
})
export class AppRoutingModule { }
