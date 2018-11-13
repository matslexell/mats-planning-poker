import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { MeetingPageComponent } from './meeting-page.component';

@NgModule({
    imports: [CommonModule, RouterModule],
    declarations: [MeetingPageComponent],
    schemas: [CUSTOM_ELEMENTS_SCHEMA],
    entryComponents: []
})
export class MeetingPageModule {}
