# Backend server code

[![Codacy Badge](https://api.codacy.com/project/badge/Grade/9df8a17484c449fda448f8fd8e478437)](https://www.codacy.com/manual/MickAvery/cpen_321_project_2019?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=MickAvery/cpen_321_project_2019&amp;utm_campaign=Badge_Grade)

## Prerequisites and Setup

  * [Node JS](https://nodejs.org/en/download/)
  * [Azure Command Line Interface (CLI)](https://docs.microsoft.com/en-us/cli/azure/install-azure-cli?view=azure-cli-latest)

After installing the Azure CLI, login with the command:

```
az login
```

## Run

Start the server locally by running

```
npm start
```

## Deployment

### Via Zip Deploy

First, make a zip file of the updated project. For example, if you're on Bash then just follow the commands:

```
$ cd /path/to/backend/proj
$ zip -r updated_files.zip .
```

Next, we'll use the Azure CLI to deploy the zip file into our App Service by running the command:

```
$ az webapp deployment source config-zip --src updated_files.zip --name <app_name> --resource-group <resource_group_name>
```
