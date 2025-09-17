# ONLYOFFICE Docs for Jira Cloud

This is an integration solution that allows Jira Cloud users to work with office documents directly from the Jira
interface using ONLYOFFICE Docs editors. The project provides preview, editing, creation, and collaboration features
for documents, presentations, and spreadsheets, as well as protection and security for file transfer and storage.

## Requirements
 - ONLYOFFICE Docs Atlassian Remote
 - ONLYOFFICE Docs (Document Server)

## Development

1. Clone project from the GitHub repository:
```
git clone https://github.com/ONLYOFFICE/onlyoffice-jira-cloud
```

2. Install the project dependencies:
```
npm install
```

3. Install dependencies in Custom UI Project:
```
cd static/onlyoffice-jira-docs-forge-custom-ui
npm install
```

4. Build Custom UI Project:
```
npm run build
```

5. Install the Forge CLI globally by running:
```
npm install -g @forge/cli
```

6. Log in to the Forge CLI ([Learn more](https://developer.atlassian.com/platform/forge/getting-started-learn/#log-in-with-an-atlassian-api-token)):
```
forge login
```

7. Specify environment variables in the manifest.yml file:
```
environment:
  variables:
    - key: FORGE_APP_ID
      default: <YOUR_FORGE_APP_ID>
    - key: FORGE_REMOTE_APP_URL
      default: <YOUR_REMOTE_APP_URL>
```

8. Navigate to the app's top-level directory and deploy your app by running ([Learn more](https://developer.atlassian.com/platform/forge/build-a-hello-world-app-in-bitbucket/#install-your-app)):
```
forge deploy
```

9. Install your app by running:
```
forge install
```

10. You can start tunneling by running:
```
forge tunnel
```

## Feedback and support

In case you have any issues, questions, or suggestions for the ONLYOFFICE Docs for Jira Cloud, please refer to
the [Issues](https://github.com/ONLYOFFICE/onlyoffice-jira-cloud/issues) section.

Official project website: [www.onlyoffice.com](https://www.onlyoffice.com/).

Support forum: [forum.onlyoffice.com](https://forum.onlyoffice.com/).
