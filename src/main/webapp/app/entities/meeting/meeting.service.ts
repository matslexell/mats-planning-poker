import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import * as moment from 'moment';
import { DATE_FORMAT } from 'app/shared/constants/input.constants';
import { map } from 'rxjs/operators';

import { SERVER_API_URL } from 'app/app.constants';
import { createRequestOption } from 'app/shared';
import { IMeeting } from 'app/shared/model/meeting.model';

type EntityResponseType = HttpResponse<IMeeting>;
type EntityArrayResponseType = HttpResponse<IMeeting[]>;

@Injectable({ providedIn: 'root' })
export class MeetingService {
    private resourceUrl = SERVER_API_URL + 'api/meetings';

    constructor(private http: HttpClient) {}

    create(meeting: IMeeting): Observable<EntityResponseType> {
        const copy = this.convertDateFromClient(meeting);
        return this.http
            .post<IMeeting>(this.resourceUrl, copy, { observe: 'response' })
            .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
    }

    update(meeting: IMeeting): Observable<EntityResponseType> {
        const copy = this.convertDateFromClient(meeting);
        return this.http
            .put<IMeeting>(this.resourceUrl, copy, { observe: 'response' })
            .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
    }

    joinMeeting(meetingUuid: String, participantName: String): Observable<any> {
        return this.http.put(`${this.resourceUrl}/join/${meetingUuid}/${participantName}`, { observe: 'response' });
    }

    find(id: number): Observable<EntityResponseType> {
        return this.http
            .get<IMeeting>(`${this.resourceUrl}/${id}`, { observe: 'response' })
            .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
    }

    getByUuid(uuid: String): Observable<EntityResponseType> {
        return this.http
            .get<IMeeting>(`${this.resourceUrl}/uuid/${uuid}`, { observe: 'response' })
            .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
    }

    query(req?: any): Observable<EntityArrayResponseType> {
        const options = createRequestOption(req);
        return this.http
            .get<IMeeting[]>(this.resourceUrl, { params: options, observe: 'response' })
            .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
    }

    delete(id: number): Observable<HttpResponse<any>> {
        return this.http.delete<any>(`${this.resourceUrl}/${id}`, { observe: 'response' });
    }

    private convertDateFromClient(meeting: IMeeting): IMeeting {
        const copy: IMeeting = Object.assign({}, meeting, {
            createdDate: meeting.createdDate != null && meeting.createdDate.isValid() ? meeting.createdDate.toJSON() : null
        });
        return copy;
    }

    private convertDateFromServer(res: EntityResponseType): EntityResponseType {
        res.body.createdDate = res.body.createdDate != null ? moment(res.body.createdDate) : null;
        return res;
    }

    private convertDateArrayFromServer(res: EntityArrayResponseType): EntityArrayResponseType {
        res.body.forEach((meeting: IMeeting) => {
            meeting.createdDate = meeting.createdDate != null ? moment(meeting.createdDate) : null;
        });
        return res;
    }
}
