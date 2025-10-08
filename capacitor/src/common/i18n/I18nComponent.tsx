import { type ReactNode } from "react";
import { IntlProvider } from 'react-intl';
import { useI18nJson, useI18nLocale } from '@/common/i18n';
import { observer, useMobxEffect, useMobxState } from 'mobx-react-use-autorun';

type Props = {
    children: ReactNode;
}

export default observer((props: Props) => {
    const state = useMobxState({
    }, {
        I18nLocale: useI18nLocale(),
        I18nJson: useI18nJson(),
    });

    useMobxEffect(() => {
        window.document.getElementsByTagName("html")[0].setAttribute('lang', state.I18nLocale);
    }, [state.I18nLocale]);

    return (
        <IntlProvider messages={state.I18nJson} locale={state.I18nLocale}>
            {props.children}
        </IntlProvider>
    );
})