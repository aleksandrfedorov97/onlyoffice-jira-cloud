# ONLYOFFICE app for Jira Cloud

This is an integration solution that allows Jira Cloud users to work with office documents directly from the Jira
interface using ONLYOFFICE Docs editors. The project provides preview, editing, creation, and collaboration features
for documents, presentations, and spreadsheets, as well as protection and security for file transfer and storage.

## Features
* View and edit documents (DOCX, XLSX, PPTX, etc.), presentations, and spreadsheets directly in a Jira issue.
* Co-edit documents in real-time: use two co-editing modes (Fast and Strict), Track Changes, comments, and built-in chat.
* Create new files directly from Jira using ONLYOFFICE (new documents, tables, presentations).

Supported formats:
See [ONLYOFFICE Docs Atlassian Remote -> Supported Formats](https://github.com/ONLYOFFICE/docs-atlassian-remote/remote?tab=readme-ov-file#supported-formats)

## Requirements
 - ONLYOFFICE Docs Atlassian Remote
 - ONLYOFFICE Docs (Document Server)

## Installing ONLYOFFICE Docs

You will need an instance of ONLYOFFICE Docs (Document Server) that is resolvable and connectable both from Jira and any end clients. ONLYOFFICE Document Server must also be able to POST to Jira directly.

You can install free Community version of ONLYOFFICE Docs or scalable Enterprise Edition with pro features.

To install free Community version, use [Docker](https://github.com/onlyoffice/Docker-DocumentServer) (recommended) or follow [these instructions](https://helpcenter.onlyoffice.com/installation/docs-community-install-ubuntu.aspx) for Debian, Ubuntu, or derivatives.

To install Enterprise Edition, follow the instructions [here](https://helpcenter.onlyoffice.com/installation/docs-enterprise-index.aspx).

Community Edition vs Enterprise Edition comparison can be found [here](#onlyoffice-docs-editions).

## Installing ONLYOFFICE app for Jira Cloud
1. Go to [marketplace.atlassian.com](http://marketplace.atlassian.com/).
2. Сhose the ONLYOFFICE app for Jira Cloud and select the Get it now button.
3. On the pop-up screen, choose a site to install your app, and which edition to install, if the app offers multiple
4. editions.

##  Configuring the integration app
Administrators can configure ONLYOFFICE integration app via the Manage Apps section in Jira Cloud (Apps -> Connected apps -> ONLYOFFICE app for Jira Cloud -> Configure). On the configuration page, set up:

* **ONLYOFFICE Docs address**: The URL of the installed ONLYOFFICE Document Server.

* **ONLYOFFICE Docs secret key**: Enables JWT to protect your documents from unauthorized access (further information can be found here).

* **Authorization header**.

## Using ONLYOFFICE app for Jira
With the ONLYOFFICE app, you can view, edit and co-author office files attached to tasks right within your Jira dashboard.

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

## ONLYOFFICE Docs editions

ONLYOFFICE offers different versions of its online document editors that can be deployed on your own servers.

**ONLYOFFICE Docs** packaged as Document Server:

* Community Edition (`onlyoffice-documentserver` package)
* Enterprise Edition (`onlyoffice-documentserver-ee` package)

The table below will help you make the right choice.

| Pricing and licensing | Community Edition | Enterprise Edition |
| ------------- | ------------- | ------------- |
| | [Get it now](https://www.onlyoffice.com/download-docs.aspx?utm_source=github&utm_medium=cpc&utm_campaign=GitHubConfluenceCloud#docs-community)  | [Start Free Trial](https://www.onlyoffice.com/download-docs.aspx?utm_source=github&utm_medium=cpc&utm_campaign=GitHubConfluenceCloud#docs-enterprise)  |
| Cost  | FREE  | [Go to the pricing page](https://www.onlyoffice.com/docs-enterprise-prices.aspx?utm_source=github&utm_medium=cpc&utm_campaign=GitHubConfluenceCloud)  |
| Simultaneous connections | up to 20 maximum  | As in chosen pricing plan |
| Number of users | up to 20 recommended | As in chosen pricing plan |
| License | GNU AGPL v.3 | Proprietary |
| **Support** | **Community Edition** | **Enterprise Edition** |
| Documentation | [Help Center](https://helpcenter.onlyoffice.com/installation/docs-community-index.aspx) | [Help Center](https://helpcenter.onlyoffice.com/installation/docs-enterprise-index.aspx) |
| Standard support | [GitHub](https://github.com/ONLYOFFICE/DocumentServer/issues) or paid | One year support included |
| Premium support | [Contact us](mailto:sales@onlyoffice.com) | [Contact us](mailto:sales@onlyoffice.com) |
| **Services** | **Community Edition** | **Enterprise Edition** |
| Conversion Service                | + | + |
| Document Builder Service          | + | + |
| **Interface** | **Community Edition** | **Enterprise Edition** |
| Tabbed interface                       | + | + |
| Dark theme                             | + | + |
| 125%, 150%, 175%, 200% scaling         | + | + |
| White Label                            | - | - |
| Integrated test example (node.js)      | + | + |
| Mobile web editors                     | - | +* |
| **Plugins & Macros** | **Community Edition** | **Enterprise Edition** |
| Plugins                           | + | + |
| Macros                            | + | + |
| **Collaborative capabilities** | **Community Edition** | **Enterprise Edition** |
| Two co-editing modes              | + | + |
| Comments                          | + | + |
| Built-in chat                     | + | + |
| Review and tracking changes       | + | + |
| Display modes of tracking changes | + | + |
| Version history                   | + | + |
| **Document Editor features** | **Community Edition** | **Enterprise Edition** |
| Font and paragraph formatting   | + | + |
| Object insertion                | + | + |
| Adding Content control          | + | + | 
| Editing Content control         | + | + | 
| Layout tools                    | + | + |
| Table of contents               | + | + |
| Navigation panel                | + | + |
| Mail Merge                      | + | + |
| Comparing Documents             | + | + |
| **Spreadsheet Editor features** | **Community Edition** | **Enterprise Edition** |
| Font and paragraph formatting   | + | + |
| Object insertion                | + | + |
| Functions, formulas, equations  | + | + |
| Table templates                 | + | + |
| Pivot tables                    | + | + |
| Data validation           | + | + |
| Conditional formatting          | + | + |
| Sparklines                   | + | + |
| Sheet Views                     | + | + |
| **Presentation Editor features** | **Community Edition** | **Enterprise Edition** |
| Font and paragraph formatting   | + | + |
| Object insertion                | + | + |
| Transitions                     | + | + |
| Animations                      | + | + |
| Presenter mode                  | + | + |
| Notes                           | + | + |
| **Form creator features** | **Community Edition** | **Enterprise Edition** |
| Adding form fields           | + | + |
| Form preview                    | + | + |
| Saving as PDF                   | + | + |
| **Working with PDF**      | **Community Edition** | **Enterprise Edition** |
| Text annotations (highlight, underline, cross out) | + | + |
| Comments                        | + | + |
| Freehand drawings               | + | + |
| Form filling                    | + | + |
| | [Get it now](https://www.onlyoffice.com/download-docs.aspx?utm_source=github&utm_medium=cpc&utm_campaign=GitHubConfluenceCloud#docs-community)  | [Start Free Trial](https://www.onlyoffice.com/download-docs.aspx?utm_source=github&utm_medium=cpc&utm_campaign=GitHubConfluenceCloud#docs-enterprise)  |

\* If supported by DMS.

In case of technical problems, the best way to get help is to submit your issues [here](https://github.com/ONLYOFFICE/onlyoffice-confluence-cloud/issues). Alternatively, you can contact ONLYOFFICE team on [forum.onlyoffice.com](https://forum.onlyoffice.com/).