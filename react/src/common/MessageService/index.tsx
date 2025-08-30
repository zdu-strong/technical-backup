import { v7 } from 'uuid';
import { observable } from 'mobx-react-use-autorun'
import { FormattedMessage } from 'react-intl';
import { GlobalExactMessageMatch } from '@common/MessageService/js/GlobalExactMessageMatch'
import { type ReactNode } from 'react';
import { getFuzzyMessageMatch } from '@common/MessageService/js/GlobalFuzzyMessageMatch';
import en_US_JSON from '@/i18n/en-US.json'

export const MessageService = {
  error: (message: string | string[] | Error | Error[] | any) => {
    handleMessage(MESSAGE_TYPE_ENUM.error, message)
  },
  warning: (message: string | string[] | any) => {
    handleMessage(MESSAGE_TYPE_ENUM.warning, message)
  },
  info: (message: string | string[] | any) => {
    handleMessage(MESSAGE_TYPE_ENUM.info, message)
  },
  success: (message: string | string[] | any) => {
    handleMessage(MESSAGE_TYPE_ENUM.success, message)
  }
}

export type MESSAGE_TYPE = "error" | "warning" | "info" | "success";

export const GlobalMessageList = observable([]) as { id: string, message: ReactNode, type: MESSAGE_TYPE }[];

export const MESSAGE_TYPE_ENUM = {
  error: "error" as MESSAGE_TYPE,
  warning: "warning" as MESSAGE_TYPE,
  info: "info" as MESSAGE_TYPE,
  success: "success" as MESSAGE_TYPE,
}

export function getMessageObject(type: MESSAGE_TYPE, message: any) {
  const messageString = getMessageString(message);
  const messageOfI18n = getI18nMessageReactNode(messageString);
  return {
    id: v7(),
    message: messageOfI18n,
    type: type
  }
}

function getI18nMessageReactNode(message: string): ReactNode {
  if (GlobalExactMessageMatch[message]) {
    return <FormattedMessage id={GlobalExactMessageMatch[message]} defaultMessage={message} />
  }

  for (const [key, value] of Object.entries(en_US_JSON)) {
    if (value === message) {
      return <FormattedMessage id={key} defaultMessage={value} />
    }
  }

  return getFuzzyMessageMatch(message);
}

function handleMessage(type: MESSAGE_TYPE, message: string | string[] | Error | Error[] | any) {
  if (message instanceof Array) {
    GlobalMessageList.splice(0, GlobalMessageList.length);
    for (const messageItem of message) {
      handleMessage(type, messageItem)
    }
  } else {
    GlobalMessageList.push(getMessageObject(type, message))
  }
}

function getMessageString(message: any): string {
  if (typeof message === "object" && message instanceof Array) {
    return getMessageString(message![0]);
  }

  let messageContent = "Network Error";
  if (typeof message === "string" && message) {
    messageContent = message;
  } else if (typeof message === "number") {
    messageContent = String(message);
  } else if (typeof message === "object" && typeof message!.message === "string" && message!.message) {
    messageContent = message.message;
  } else if (typeof message === "object" && typeof message!.error === "string" && message!.error) {
    messageContent = message.error;
  }
  return messageContent;
}
