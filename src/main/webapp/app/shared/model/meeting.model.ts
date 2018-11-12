import { Moment } from 'moment';
import { IParticipant } from 'app/shared/model//participant.model';

export interface IMeeting {
    id?: number;
    name?: string;
    uuid?: string;
    createdDate?: Moment;
    participants?: IParticipant[];
}

export class Meeting implements IMeeting {
    constructor(
        public id?: number,
        public name?: string,
        public uuid?: string,
        public createdDate?: Moment,
        public participants?: IParticipant[]
    ) {}
}
