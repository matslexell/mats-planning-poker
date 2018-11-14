import { Component, OnInit } from '@angular/core';
import { NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';

import { LoginModalService, Principal, Account } from 'app/core';
import { Meeting } from 'app/shared/model/meeting.model';
import { Participant } from 'app/shared/model/participant.model';
import { Router } from '@angular/router';
import { MeetingService } from 'app/entities/meeting';

@Component({
    selector: 'jhi-home',
    templateUrl: './home.component.html',
    styleUrls: ['home.scss']
})
export class HomeComponent implements OnInit {
    account: Account;
    modalRef: NgbModalRef;
    newMeeting: Meeting = {};
    creatorParticipant: Participant = {};

    existingMeeting: Meeting = {};
    joinerParticipant: Participant = {};

    constructor(
        private principal: Principal,
        private loginModalService: LoginModalService,
        private eventManager: JhiEventManager,
        private router: Router,
        private meetingService: MeetingService
    ) {}

    ngOnInit() {
        this.principal.identity().then(account => {
            this.account = account;
        });
        this.registerAuthenticationSuccess();
    }

    registerAuthenticationSuccess() {
        this.eventManager.subscribe('authenticationSuccess', message => {
            this.principal.identity().then(account => {
                this.account = account;
            });
        });
    }

    isAuthenticated() {
        return this.principal.isAuthenticated();
    }

    login() {
        this.modalRef = this.loginModalService.open();
    }

    createMeeting() {
        console.log('createMeeting', this.newMeeting, this.creatorParticipant);
        this.newMeeting.uuid = 'abc123'; // Todo, create meeting
        this.meetingService.createFromName(this.newMeeting.name).subscribe(meeting => {
            this.router.navigate(['/planningPokerMeeting/' + meeting.uuid], {
                queryParams: { participantName: this.creatorParticipant.name }
            });
        });
    }

    joinMeeting() {
        console.log('joinMeeting', this.existingMeeting, this.joinerParticipant);
        this.router.navigate(['/planningPokerMeeting/' + this.existingMeeting.uuid], {
            queryParams: { participantName: this.joinerParticipant.name }
        });
    }
}
