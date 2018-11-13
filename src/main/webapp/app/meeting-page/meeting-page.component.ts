import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Location } from '@angular/common';
import { Meeting } from 'app/shared/model/meeting.model';
import { SessionStorageService } from 'ngx-webstorage';
import { MeetingService } from 'app/entities/meeting';

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

    constructor(
        private activatedRoute: ActivatedRoute,
        private location: Location,
        private router: Router,
        private $sessionStorage: SessionStorageService,
        private meetingService: MeetingService
    ) {}

    public ngOnInit(): void {
        this.meeting = {
            name: 'my meeting',
            participants: [
                { name: 'mats', vote: '1/2' },
                { name: 'ida', vote: '5' },
                { name: 'axel', vote: '5' },
                { name: 'martin', vote: 'no vote' },
                { name: 'robert', vote: '1' }
            ],
            uuid: '8kvg3mdslm3v'
        };

        this.meetingService.joinMeeting('123', 'mats').subscribe(jwt => {
            console.log('JWT is ', jwt);
            this.$sessionStorage.store('authenticationToken', jwt);
        });

        this.results = this.calculateResult(this.meeting, this.planningPokerValues);

        console.log('Results', this.results);

        const meetingUuid = this.activatedRoute.snapshot.paramMap.get('meetingUuid');

        this.meetingService.getByUuid(meetingUuid).subscribe(meeting => {
            console.log('Meeting loaded by uuid', meeting);
        });

        const url =
            this.router.url.lastIndexOf('?') === -1 ? this.router.url : this.router.url.substring(0, this.router.url.lastIndexOf('?')); // Remove query params if present

        console.log('Meeting Id:', meetingUuid);

        this.activatedRoute.queryParams.subscribe(params => {
            console.log('Participant Name:', params.participantName);
            this.location.go(url); // Set url without rerouting
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

    public submitVote() {
        console.log('submitVote', this.myVote);
    }
}
