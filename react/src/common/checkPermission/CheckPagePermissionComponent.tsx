import api from "@api";
import LoadingOrErrorComponent from "@common/MessageService/LoadingOrErrorComponent";
import { GlobalUserInfo, toSignIn } from "@common/Server";
import { observer, useMobxEffect, useMobxState, useMount } from "mobx-react-use-autorun";
import { ReactNode } from "react";
import { useNavigate } from "react-router-dom";
import { ReplaySubject, from } from "rxjs";
import { exhaustMapWithTrailing } from "rxjs-exhaustmap-with-trailing";
import { v7 } from "uuid";

export default observer((props: {
  children: ReactNode,
  isAutoLogin?: boolean,
  checkIsSignIn?: boolean,
  checkIsNotSignIn?: boolean,
}) => {

  const state = useMobxState(() => ({
    subject: new ReplaySubject<void>(1),
    error: null as any,
    hasInitAccessToken: false,
  }), {
    navigate: useNavigate()
  })

  useMount(async (subscription) => {
    subscription.add(state.subject.pipe(
      exhaustMapWithTrailing(() => from((async () => {
        try {
          state.error = null;
          await handleIsAutoSignIn();
          handleIsSignIn();
          handleCheckIsNotSignIn();
        } catch (error) {
          state.error = error;
        }
      })()))
    ).subscribe());
  })

  useMobxEffect(() => {
    state.subject.next();
  }, [props.isAutoLogin, props.checkIsSignIn, props.checkIsNotSignIn, GlobalUserInfo.accessToken])

  function handleCheckIsNotSignIn() {
    if (props.checkIsNotSignIn && props.checkIsSignIn) {
      throw new Error("Must check if sign in")
    }
    if (props.checkIsNotSignIn && GlobalUserInfo.accessToken) {
      state.navigate("/");
    }
  }

  function handleIsSignIn() {
    if (props.checkIsSignIn && props.checkIsNotSignIn) {
      throw new Error("Must check if sign in")
    }

    if (props.checkIsSignIn && !GlobalUserInfo.accessToken) {
      toSignIn();
    }
  }

  async function handleIsAutoSignIn() {
    if (props.isAutoLogin && !props.checkIsSignIn) {
      throw new Error("Must check if sign in")
    }
    if (props.isAutoLogin && props.checkIsNotSignIn) {
      throw new Error("Must check if sign in")
    }
    if (props.isAutoLogin && !state.hasInitAccessToken && !GlobalUserInfo.accessToken) {
      await api.Authorization.signUp(v7(), "visitor", []);
    }
    if (!state.hasInitAccessToken && GlobalUserInfo.accessToken) {
      state.hasInitAccessToken = true;
    }
  }

  function isReady() {
    if (props.checkIsSignIn && !GlobalUserInfo.accessToken) {
      return false;
    }
    if (props.checkIsNotSignIn && GlobalUserInfo.accessToken) {
      return false;
    }
    return true;
  }

  return <LoadingOrErrorComponent ready={isReady()} error={state.error} >
    {props.children}
  </LoadingOrErrorComponent>
})