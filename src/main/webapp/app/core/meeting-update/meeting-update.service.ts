import { Injectable, Output, EventEmitter } from '@angular/core';
import { Router } from '@angular/router';
import { Observable, Observer, Subscription } from 'rxjs';

import { AuthServerProvider } from '../auth/auth-jwt.service';

import * as SockJS from 'sockjs-client';
import * as Stomp from 'webstomp-client';
import { WindowRef } from '../tracker/window.service';
import { JhiEventManager } from 'ng-jhipster';

@Injectable({ providedIn: 'root' })
export class MeetingUpdateService {
    stompClient = null;
    subscriber = null;
    connection: Promise<any>;
    connectedPromise: any;
    listener: Observable<any>;
    listenerObserver: Observer<any>;
    alreadyConnectedOnce = false;
    private subscription: Subscription;

    constructor(
        private router: Router,
        private authServerProvider: AuthServerProvider,
        private $window: WindowRef,
        private eventManager: JhiEventManager
    ) {
        this.connection = this.createConnection();
        this.listener = this.createListener();
    }

    connect(meetingUuid: String, token: String) {
        if (this.connectedPromise === null) {
            this.connection = this.createConnection();
        }
        // building absolute path so that websocket doesn't fail when deploying with a context path
        const loc = this.$window.nativeWindow.location;
        let url;
        url = '//' + loc.host + loc.pathname + 'websocket/tracker';
        const authToken = this.authServerProvider.getToken();
        if (authToken) {
            url += '?access_token=' + authToken;
        }
        const socket = new SockJS(url);
        this.stompClient = Stomp.over(socket);
        const headers = {};
        this.stompClient.connect(headers, () => {
            this.connectedPromise('success');
            this.connectedPromise = null;
            this.sendActivity(meetingUuid, token);
        });
    }

    disconnect() {
        if (this.stompClient !== null) {
            this.stompClient.disconnect();
            this.stompClient = null;
        }
        if (this.subscription) {
            this.subscription.unsubscribe();
            this.subscription = null;
        }
        this.alreadyConnectedOnce = false;
    }

    receive() {
        return this.listener;
    }

    sendActivity(meetingUuid: String, token: String) {
        if (this.stompClient !== null && this.stompClient.connected) {
            this.stompClient.send(
                `/meetingUpdate/server/${meetingUuid}/${token}`, // destination
                JSON.stringify({ page: this.router.routerState.snapshot.url }), // body
                {} // header
            );
        }
    }

    subscribe(meetingUuid: String) {
        this.connection.then(() => {
            this.subscriber = this.stompClient.subscribe('/meetingUpdate/client/' + meetingUuid, () => {
                this.eventManager.broadcast({ name: 'meetingUpdate', content: 'OK' });
            });
        });
    }

    unsubscribe() {
        if (this.subscriber !== null) {
            this.subscriber.unsubscribe();
        }
        this.listener = this.createListener();
    }

    private createListener(): Observable<any> {
        return new Observable(observer => {
            this.listenerObserver = observer;
        });
    }

    private createConnection(): Promise<any> {
        return new Promise((resolve, reject) => (this.connectedPromise = resolve));
    }
}
