import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Location } from '@angular/common';
import { Meeting } from 'app/shared/model/meeting.model';
import { SessionStorageService } from 'ngx-webstorage';
import { MeetingService } from 'app/entities/meeting';
import { ParticipantService } from 'app/entities/participant';
import { LoginService, JhiTrackerService } from 'app/core';
import { Participant } from 'app/shared/model/participant.model';
import { MeetingUpdateService } from 'app/core/meeting-update/meeting-update.service';
import { JhiEventManager } from 'ng-jhipster';

@Component({
    selector: 'jhi-meeting-page',
    templateUrl: './meeting-page.component.html',
    styleUrls: ['meeting-page.scss']
})
export class MeetingPageComponent implements OnInit, OnDestroy {
    planningPokerValues = ['0', '1/2', '1', '2', '3', '5', '8', '13'];

    meeting: Meeting = {};
    url: String;
    myVote: String = '';
    results: { value: String; count: Number }[];
    token: String;

    constructor(
        private activatedRoute: ActivatedRoute,
        private location: Location,
        private router: Router,
        private meetingService: MeetingService,
        private participantService: ParticipantService,
        private meetingUpdateService: MeetingUpdateService,
        private eventManager: JhiEventManager
    ) {}

    public ngOnInit(): void {
        this.disconnect();

        this.meeting.uuid = this.activatedRoute.snapshot.paramMap.get('meetingUuid');

        this.activatedRoute.queryParams.subscribe(params => {
            this.meetingService.joinMeeting(this.meeting.uuid, params.participantName).subscribe(jwt => {
                console.log('JWT FROM SERVER', jwt);
                this.token = jwt;
                this.update();
                this.connect(this.meeting.uuid, this.token);
            });
            this.location.go(this.getLocationWithoutParams()); // Set url without rerouting
            this.url = window.location.href;
        });
    }

    public ngOnDestroy(): void {
        console.log('disconnect from group');
        this.meetingUpdateService.unsubscribe();
        this.meetingUpdateService.disconnect();
    }

    private calculateResult(meeting: Meeting, planningPokerValues: String[]): { value: String; count: Number }[] {
        const results = planningPokerValues.map(planningPokerValue => {
            return {
                value: planningPokerValue,
                count: meeting.participants.map(participant => participant.vote).filter(vote => vote === planningPokerValue).length
            };
        });
        const noVoteCount = meeting.participants.filter(participant => planningPokerValues.find(a => a === participant.vote) === undefined)
            .length;
        return results.concat({ value: 'No vote', count: noVoteCount });
    }

    public update(): void {
        this.meetingService.getByUuid(this.meeting.uuid).subscribe(data => {
            console.log('Meeting loaded by uuid', data.body);
            this.meeting = data.body;
            this.results = this.calculateResult(this.meeting, this.planningPokerValues);
        });
    }

    public connect(meetingUuid: String, token: String) {
        this.meetingUpdateService.connect(meetingUuid, token);
        this.meetingUpdateService.subscribe(meetingUuid);
        this.eventManager.subscribe('meetingUpdate', response => {
            this.update();
        });
    }

    public disconnect() {
        this.meetingUpdateService.unsubscribe();
        this.meetingUpdateService.disconnect();
    }

    public sendActivity() {
        this.meetingUpdateService.sendActivity(this.meeting.uuid, this.token);
    }

    public submitVote() {
        console.log('submitVote', this.myVote);
        this.participantService.updateFromVote(this.myVote, this.token).subscribe(data => {
            this.sendActivity();
        });
    }

    private getLocationWithoutParams(): string {
        return this.router.url.lastIndexOf('?') === -1 ? this.router.url : this.router.url.substring(0, this.router.url.lastIndexOf('?'));
    }
}
