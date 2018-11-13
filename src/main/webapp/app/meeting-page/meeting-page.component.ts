import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Location } from '@angular/common';
import { Meeting } from 'app/shared/model/meeting.model';

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

    constructor(private activatedRoute: ActivatedRoute, private location: Location, private router: Router) {}

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

        this.results = this.calculateResult(this.meeting, this.planningPokerValues);

        console.log('Results', this.results);

        const meetingId = this.activatedRoute.snapshot.paramMap.get('meetingId');

        const url =
            this.router.url.lastIndexOf('?') === -1 ? this.router.url : this.router.url.substring(0, this.router.url.lastIndexOf('?')); // Remove query params if present

        console.log('Meeting Id:', meetingId);

        this.activatedRoute.queryParams.subscribe(params => {
            console.log('Participant Name:', params.participantName);
            this.location.go(url); // Set url without rerouting
            this.url = window.location.href;
        });
    }

    private calculateResult(meeting: Meeting, planningPokerValues: String[]): { value: String; count: Number }[] {
        return planningPokerValues.map(planningPokerValue => {
            return {
                value: planningPokerValue,
                count: meeting.participants.map(participant => participant.vote).filter(vote => vote === planningPokerValue).length
            };
        });
    }

    public submitVote() {
        console.log('submitVote', this.myVote);
    }
}
