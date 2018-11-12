import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, RouterStateSnapshot, Routes } from '@angular/router';
import { UserRouteAccessService } from 'app/core';
import { of } from 'rxjs';
import { map } from 'rxjs/operators';
import { Participant } from 'app/shared/model/participant.model';
import { ParticipantService } from './participant.service';
import { ParticipantComponent } from './participant.component';
import { ParticipantDetailComponent } from './participant-detail.component';
import { ParticipantUpdateComponent } from './participant-update.component';
import { ParticipantDeletePopupComponent } from './participant-delete-dialog.component';
import { IParticipant } from 'app/shared/model/participant.model';

@Injectable({ providedIn: 'root' })
export class ParticipantResolve implements Resolve<IParticipant> {
    constructor(private service: ParticipantService) {}

    resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot) {
        const id = route.params['id'] ? route.params['id'] : null;
        if (id) {
            return this.service.find(id).pipe(map((participant: HttpResponse<Participant>) => participant.body));
        }
        return of(new Participant());
    }
}

export const participantRoute: Routes = [
    {
        path: 'participant',
        component: ParticipantComponent,
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'Participants'
        },
        canActivate: [UserRouteAccessService]
    },
    {
        path: 'participant/:id/view',
        component: ParticipantDetailComponent,
        resolve: {
            participant: ParticipantResolve
        },
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'Participants'
        },
        canActivate: [UserRouteAccessService]
    },
    {
        path: 'participant/new',
        component: ParticipantUpdateComponent,
        resolve: {
            participant: ParticipantResolve
        },
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'Participants'
        },
        canActivate: [UserRouteAccessService]
    },
    {
        path: 'participant/:id/edit',
        component: ParticipantUpdateComponent,
        resolve: {
            participant: ParticipantResolve
        },
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'Participants'
        },
        canActivate: [UserRouteAccessService]
    }
];

export const participantPopupRoute: Routes = [
    {
        path: 'participant/:id/delete',
        component: ParticipantDeletePopupComponent,
        resolve: {
            participant: ParticipantResolve
        },
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'Participants'
        },
        canActivate: [UserRouteAccessService],
        outlet: 'popup'
    }
];
