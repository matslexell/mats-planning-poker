import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Location } from '@angular/common';
import { Meeting } from 'app/shared/model/meeting.model';
import { SessionStorageService } from 'ngx-webstorage';
import { MeetingService } from 'app/entities/meeting';
import { ParticipantService } from 'app/entities/participant';
import { LoginService } from 'app/core';
import { Participant } from 'app/shared/model/participant.model';

@Component({
    selector: 'jhi-meeting-page',
    templateUrl: './meeting-page.component.html',
    styleUrls: ['meeting-page.scss']
})
export class MeetingPageComponent implements OnInit {
    planningPokerValues = ['0', '1/2', '1', '2', '3', '5', '8', '13'];

    meeting: Meeting = {};
    url: String;
    myVote: String = '';
    results: any;
    token: String;

    constructor(
        private activatedRoute: ActivatedRoute,
        private location: Location,
        private router: Router,
        private meetingService: MeetingService,
        private participantService: ParticipantService
    ) {}

    public ngOnInit(): void {
        // this.meeting = {
        //     name: 'my meeting',
        //     participants: [
        //         { name: 'mats', vote: '1/2' },
        //         { name: 'ida', vote: '5' },
        //         { name: 'axel', vote: '5' },
        //         { name: 'martin', vote: 'no vote' },
        //         { name: 'robert', vote: '1' }
        //     ],
        //     uuid: '8kvg3mdslm3v'
        // };

        const meetingUuid = this.activatedRoute.snapshot.paramMap.get('meetingUuid');

        this.activatedRoute.queryParams.subscribe(params => {
            this.meetingService.joinMeeting(meetingUuid, params.participantName).subscribe(jwt => {
                console.log('JWT FROM SERVER', jwt);
                this.token = jwt;
                this.update(meetingUuid);
            });
            this.location.go(this.getLocationWithoutParams()); // Set url without rerouting
            this.url = window.location.href;
        });
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

    public update(meetingUuid: string): void {
        this.meetingService.getByUuid(meetingUuid).subscribe(data => {
            console.log('Meeting loaded by uuid', data.body);
            this.meeting = data.body;
            this.results = this.calculateResult(this.meeting, this.planningPokerValues);
        });
    }

    public submitVote() {
        console.log('submitVote', this.myVote);
        this.participantService.updateFromVote(this.myVote, this.token).subscribe(data => {
            console.log('UPDATE PARTICIPANT', data);
            this.update(this.meeting.uuid); // todo necessary when websocket is in place?
        });
    }

    private getLocationWithoutParams(): string {
        return this.router.url.lastIndexOf('?') === -1 ? this.router.url : this.router.url.substring(0, this.router.url.lastIndexOf('?'));
    }
}
