import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';

import { MatsPlanningPokerMeetingModule } from './meeting/meeting.module';
import { MatsPlanningPokerParticipantModule } from './participant/participant.module';
/* jhipster-needle-add-entity-module-import - JHipster will add entity modules imports here */

@NgModule({
    // prettier-ignore
    imports: [
        MatsPlanningPokerMeetingModule,
        MatsPlanningPokerParticipantModule,
        /* jhipster-needle-add-entity-module - JHipster will add entity modules here */
    ],
    declarations: [],
    entryComponents: [],
    providers: [],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class MatsPlanningPokerEntityModule {}
