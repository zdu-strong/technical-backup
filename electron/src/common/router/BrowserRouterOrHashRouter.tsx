import { observer } from "mobx-react-use-autorun";
import React from "react";
import { BrowserRouter, HashRouter } from 'react-router-dom';

type Props = {
    children: React.ReactNode;
}

export default observer((props: Props) => {

    if (process.env.NODE_ENV === "production") {
        return (<HashRouter>{props.children}</HashRouter>);
    } else {
        return (<BrowserRouter>{props.children}</BrowserRouter>);
    }
})