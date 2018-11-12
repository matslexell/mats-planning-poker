import { NgModule } from '@angular/core';

import { MatsPlanningPokerSharedLibsModule, JhiAlertComponent, JhiAlertErrorComponent } from './';

@NgModule({
    imports: [MatsPlanningPokerSharedLibsModule],
    declarations: [JhiAlertComponent, JhiAlertErrorComponent],
    exports: [MatsPlanningPokerSharedLibsModule, JhiAlertComponent, JhiAlertErrorComponent]
})
export class MatsPlanningPokerSharedCommonModule {}
