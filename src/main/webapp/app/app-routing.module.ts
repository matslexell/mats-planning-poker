import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { errorRoute, navbarRoute } from './layouts';
import { DEBUG_INFO_ENABLED } from 'app/app.constants';
import { meetingPageRoute } from './meeting-page';

const LAYOUT_ROUTES = [navbarRoute, ...errorRoute];

const CUSTOM_ROUTES = [...meetingPageRoute];

@NgModule({
    imports: [
        RouterModule.forRoot(
            [
                ...LAYOUT_ROUTES,
                {
                    path: 'admin',
                    loadChildren: './admin/admin.module#MatsPlanningPokerAdminModule'
                }
            ],
            { useHash: true, enableTracing: DEBUG_INFO_ENABLED }
        ),
        RouterModule.forRoot(CUSTOM_ROUTES, { useHash: true, enableTracing: DEBUG_INFO_ENABLED })
    ],
    exports: [RouterModule]
})
export class MatsPlanningPokerAppRoutingModule {}
