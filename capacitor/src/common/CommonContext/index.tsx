import { useMobxState } from "mobx-react-use-autorun";
import { useIntl } from "react-intl";
import { useNavigate, useSearchParams } from 'react-router-dom'

export function useCommonContext() {

  const intl = useIntl();

  const navigate = useNavigate();

  const [urlSearchParams, setURLSearchParams] = useSearchParams();

  const state = useMobxState({

  }, {
    intl,
    navigate,
    urlSearchParams,
    setURLSearchParams,
    formatMessage: intl.formatMessage
  });

  return state;
}