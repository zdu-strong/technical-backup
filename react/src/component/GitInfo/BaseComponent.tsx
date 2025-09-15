import React from "react";
import { observable, makeObservable } from 'mobx-react-use-autorun'

// export interface BaseComponent<P = {}, S = {}, SS = any> extends ComponentLifecycle<P, S, SS> { }


export class BaseComponent<P = {}, S = {}, SS = any> extends React.Component<P, S, SS> {

  @observable
  ready: boolean = false;

  @observable
  error = null as any;

  constructor(props: any) {
    super(props);
    makeObservable(this);
  }

}