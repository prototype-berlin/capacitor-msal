// @ts-ignore
import { WebPlugin } from '@capacitor/core';
import { Configuration, UserAgentApplication } from 'msal/lib-es6';

import { MsalPlugin } from './definitions';

export class MsalWeb extends WebPlugin implements MsalPlugin {
  private userAgentApplication: UserAgentApplication;

  constructor() {
    super({
      name: 'Msal',
      platforms: ['web']
    });
  }

  public async getAccessToken(options: { clientId: string, authority: string, scopes: string[] }): Promise<{ accessToken: string }> {
    this.userAgentApplication = new UserAgentApplication(this.getClientConfiguration(options));

    try {
      const authResponse = await this.userAgentApplication.loginPopup({ scopes: options.scopes });

      return { accessToken: authResponse.idToken.rawIdToken };
    } catch (error) {
      return Promise.reject(error);
    }
  }

  public async getAccessTokenSilently(options: { clientId: string, authority: string, scopes: string[] }): Promise<{ accessToken?: string }> {
    this.userAgentApplication = new UserAgentApplication(this.getClientConfiguration(options));

    try {
      const authResponse = await this.userAgentApplication.acquireTokenSilent({ scopes: options.scopes });
    
      return { accessToken: authResponse.idToken.rawIdToken };
    } catch (error) {
      return {};
    }
  }

  public async signOut(): Promise<void> {
    if (!this.userAgentApplication) { return; }

    this.userAgentApplication.logout();
  }

  private getClientConfiguration(options: { clientId: string, authority: string, scopes: string[] }): Configuration {
    return {
      auth: {
        clientId: options.clientId,
        authority: options.authority,
        redirectUri: window.location.protocol + '//' + window.location.host,
        validateAuthority: false,
      }
    };
  }
}

const Msal = new MsalWeb();

export { Msal };

// @ts-ignore
import { registerWebPlugin } from '@capacitor/core';
registerWebPlugin(Msal);
