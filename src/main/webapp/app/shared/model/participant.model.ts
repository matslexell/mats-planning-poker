export interface IParticipant {
    id?: number;
    name?: string;
    vote?: string;
    token?: string;
    meetingName?: string;
    meetingId?: number;
}

export class Participant implements IParticipant {
    constructor(
        public id?: number,
        public name?: string,
        public vote?: string,
        public token?: string,
        public meetingName?: string,
        public meetingId?: number
    ) {}
}
