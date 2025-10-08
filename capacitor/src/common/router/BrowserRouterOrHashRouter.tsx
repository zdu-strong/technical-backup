import { observer } from "mobx-react-use-autorun";
import React, { type ReactNode } from "react";
import { BrowserRouter, HashRouter } from 'react-router-dom';

type Props = {
    children: ReactNode;
}

export default observer((props: Props) => {

    if (process.env.NODE_ENV === "production") {
        return (<HashRouter>{props.children}</HashRouter>);
    } else {
        return (<BrowserRouter>{props.children}</BrowserRouter>);
    }
})
