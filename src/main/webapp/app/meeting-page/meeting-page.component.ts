import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Location } from '@angular/common';

@Component({
    selector: 'jhi-meeting-page',
    templateUrl: './meeting-page.component.html'
})
export class MeetingPageComponent implements OnInit {
    constructor(private activatedRoute: ActivatedRoute, private location: Location, private router: Router) {}

    public ngOnInit(): void {
        const meetingId = this.activatedRoute.snapshot.paramMap.get('meetingId');

        const url =
            this.router.url.lastIndexOf('?') === -1 ? this.router.url : this.router.url.substring(0, this.router.url.lastIndexOf('?')); // Remove query params if present

        console.log('Meeting Id:', meetingId);

        this.activatedRoute.queryParams.subscribe(params => {
            console.log('Participant Name:', params.participantName);
            this.location.go(url); // Set url without rerouting
        });
    }
}
