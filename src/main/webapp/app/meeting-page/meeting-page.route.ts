import { Routes } from '@angular/router';
import { MeetingPageComponent } from './meeting-page.component';

export const meetingPageRoute: Routes = [
    {
        path: 'planningPokerMeeting/:meetingId',
        component: MeetingPageComponent,
        data: {
            authorities: [],
            pageTitle: 'meeting.name'
        }
    }
];
