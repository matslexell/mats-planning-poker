import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { MeetingPageComponent } from './meeting-page.component';
import { FormsModule } from '@angular/forms';
import { MatsPlanningPokerSharedModule } from 'app/shared';

@NgModule({
    imports: [CommonModule, MatsPlanningPokerSharedModule, RouterModule, FormsModule],
    declarations: [MeetingPageComponent],
    schemas: [CUSTOM_ELEMENTS_SCHEMA],
    entryComponents: []
})
export class MeetingPageModule {}
