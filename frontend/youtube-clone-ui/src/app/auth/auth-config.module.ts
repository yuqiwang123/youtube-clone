import { NgModule } from '@angular/core';
import {AuthModule, LogLevel} from 'angular-auth-oidc-client';


@NgModule({
    imports: [AuthModule.forRoot({
        config: {
            authority: 'https://dev-p32rlnqi.us.auth0.com',
            redirectUrl: window.location.origin,
            clientId: 'ZCb3jfETnjiNtcWkOMFUkVq7dGPKnoxt',
            scope: 'openid profile offline_access email',
            responseType: 'code',
            silentRenew: true,
            useRefreshToken: true,
            secureRoutes: ['http://localhost:8081'],
            customParamsAuthRequest: {
              audience: 'http://localhost:8081'
            },
            logLevel: LogLevel.Debug,
        }
      })],
    providers: [],
    exports: [AuthModule],
})
export class AuthConfigModule {}
