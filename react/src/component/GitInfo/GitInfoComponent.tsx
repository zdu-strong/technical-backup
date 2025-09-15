import React from 'react'
import GitInfo from 'react-git-info/macro'
import { format, parseJSON } from "date-fns";
import api from "@api";
import { GitPropertiesModel } from "@model/GitPropertiesModel";
import { FormattedMessage } from "react-intl";
import LoadingOrErrorComponent from "@common/MessageService/LoadingOrErrorComponent";
import { observable, observer, makeObservable } from 'mobx-react-use-autorun'

@observer
export class GitInfoComponent extends React.Component {

  @observable
  clientGitInfo = GitInfo();

  @observable
  serverGitInfo = null as any as GitPropertiesModel;

  @observable
  error = null as any;

  @observable
  ready: boolean = false;

  async componentDidMount() {
    this.serverGitInfo = await api.Git.getServerGitInfo();
    this.ready = true;
  }

  render() {
    return <LoadingOrErrorComponent ready={this.ready} error={this.error}>
      {
        this.ready && <>
          <div className="w-full h-full flex justify-center flex-col items-center">
            <div className="flex flex-row">
              <div className="flex flex-row">
                <FormattedMessage id="FrontEndCommitId" defaultMessage="Front-end commit id" />
                {`: `}
              </div>
              <div className="flex flex-row" id="FrontEndCommitIdInfo" style={{ marginLeft: "1em" }}>
                {this.clientGitInfo.commit.hash}
              </div>
            </div>
            <div className="flex flex-row">
              <div className="flex flex-row">
                <FormattedMessage id="BackendCommitId" defaultMessage="Backend commit id" />
                {`: `}
              </div>
              <div className="flex flex-row" id="BackendCommitIdInfo" style={{ marginLeft: "1em" }}>
                {this.serverGitInfo.commitId}
              </div>
            </div>
            <div className="flex flex-row">
              <div className="flex flex-row">
                <FormattedMessage id="FrontEndUpdateTime" defaultMessage="Front-end update time" />
                {`: `}
              </div>
              <div className="flex flex-row" id="FrondEndUpdateDateInfo" style={{ marginLeft: "1em" }}>
                {format(parseJSON(this.clientGitInfo.commit.date), "yyyy-MM-dd HH:mm")}
              </div>
            </div>
            <div className="flex flex-row">
              <div className="flex flex-row">
                <FormattedMessage id="BackendUpdateTime" defaultMessage="Backend update time" />
                {`: `}
              </div>
              <div className="flex flex-row" id="BackendUpdateDateInfo" style={{ marginLeft: "1em" }}>
                {format(this.serverGitInfo.commitDate, "yyyy-MM-dd HH:mm")}
              </div>
            </div>
          </div>
        </>
      }
    </LoadingOrErrorComponent>
  }

  constructor(props: any) {
    super(props);
    makeObservable(this);
  }

}
