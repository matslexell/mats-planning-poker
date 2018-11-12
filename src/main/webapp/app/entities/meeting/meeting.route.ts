import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, RouterStateSnapshot, Routes } from '@angular/router';
import { UserRouteAccessService } from 'app/core';
import { of } from 'rxjs';
import { map } from 'rxjs/operators';
import { Meeting } from 'app/shared/model/meeting.model';
import { MeetingService } from './meeting.service';
import { MeetingComponent } from './meeting.component';
import { MeetingDetailComponent } from './meeting-detail.component';
import { MeetingUpdateComponent } from './meeting-update.component';
import { MeetingDeletePopupComponent } from './meeting-delete-dialog.component';
import { IMeeting } from 'app/shared/model/meeting.model';

@Injectable({ providedIn: 'root' })
export class MeetingResolve implements Resolve<IMeeting> {
    constructor(private service: MeetingService) {}

    resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot) {
        const id = route.params['id'] ? route.params['id'] : null;
        if (id) {
            return this.service.find(id).pipe(map((meeting: HttpResponse<Meeting>) => meeting.body));
        }
        return of(new Meeting());
    }
}

export const meetingRoute: Routes = [
    {
        path: 'meeting',
        component: MeetingComponent,
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'Meetings'
        },
        canActivate: [UserRouteAccessService]
    },
    {
        path: 'meeting/:id/view',
        component: MeetingDetailComponent,
        resolve: {
            meeting: MeetingResolve
        },
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'Meetings'
        },
        canActivate: [UserRouteAccessService]
    },
    {
        path: 'meeting/new',
        component: MeetingUpdateComponent,
        resolve: {
            meeting: MeetingResolve
        },
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'Meetings'
        },
        canActivate: [UserRouteAccessService]
    },
    {
        path: 'meeting/:id/edit',
        component: MeetingUpdateComponent,
        resolve: {
            meeting: MeetingResolve
        },
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'Meetings'
        },
        canActivate: [UserRouteAccessService]
    }
];

export const meetingPopupRoute: Routes = [
    {
        path: 'meeting/:id/delete',
        component: MeetingDeletePopupComponent,
        resolve: {
            meeting: MeetingResolve
        },
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'Meetings'
        },
        canActivate: [UserRouteAccessService],
        outlet: 'popup'
    }
];
