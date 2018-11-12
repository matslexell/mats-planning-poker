import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { HttpResponse, HttpErrorResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import * as moment from 'moment';
import { DATE_TIME_FORMAT } from 'app/shared/constants/input.constants';

import { IMeeting } from 'app/shared/model/meeting.model';
import { MeetingService } from './meeting.service';

@Component({
    selector: 'jhi-meeting-update',
    templateUrl: './meeting-update.component.html'
})
export class MeetingUpdateComponent implements OnInit {
    private _meeting: IMeeting;
    isSaving: boolean;
    createdDate: string;

    constructor(private meetingService: MeetingService, private activatedRoute: ActivatedRoute) {}

    ngOnInit() {
        this.isSaving = false;
        this.activatedRoute.data.subscribe(({ meeting }) => {
            this.meeting = meeting;
        });
    }

    previousState() {
        window.history.back();
    }

    save() {
        this.isSaving = true;
        this.meeting.createdDate = moment(this.createdDate, DATE_TIME_FORMAT);
        if (this.meeting.id !== undefined) {
            this.subscribeToSaveResponse(this.meetingService.update(this.meeting));
        } else {
            this.subscribeToSaveResponse(this.meetingService.create(this.meeting));
        }
    }

    private subscribeToSaveResponse(result: Observable<HttpResponse<IMeeting>>) {
        result.subscribe((res: HttpResponse<IMeeting>) => this.onSaveSuccess(), (res: HttpErrorResponse) => this.onSaveError());
    }

    private onSaveSuccess() {
        this.isSaving = false;
        this.previousState();
    }

    private onSaveError() {
        this.isSaving = false;
    }
    get meeting() {
        return this._meeting;
    }

    set meeting(meeting: IMeeting) {
        this._meeting = meeting;
        this.createdDate = moment(meeting.createdDate).format(DATE_TIME_FORMAT);
    }
}
