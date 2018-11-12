import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { RouterModule } from '@angular/router';

import { MatsPlanningPokerSharedModule } from 'app/shared';
import {
    MeetingComponent,
    MeetingDetailComponent,
    MeetingUpdateComponent,
    MeetingDeletePopupComponent,
    MeetingDeleteDialogComponent,
    meetingRoute,
    meetingPopupRoute
} from './';

const ENTITY_STATES = [...meetingRoute, ...meetingPopupRoute];

@NgModule({
    imports: [MatsPlanningPokerSharedModule, RouterModule.forChild(ENTITY_STATES)],
    declarations: [
        MeetingComponent,
        MeetingDetailComponent,
        MeetingUpdateComponent,
        MeetingDeleteDialogComponent,
        MeetingDeletePopupComponent
    ],
    entryComponents: [MeetingComponent, MeetingUpdateComponent, MeetingDeleteDialogComponent, MeetingDeletePopupComponent],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class MatsPlanningPokerMeetingModule {}
