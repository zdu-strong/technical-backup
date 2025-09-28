import 'tailwindcss/utilities.css';
import '@common/app-base-css/index.css';
import '@fontsource/roboto';
import '@common/Server';
import '@common/ScreenOrentation';
import '@common/StorageManage/StorageManageRun';
import ReactDom from 'react-dom/client'
import App from '@/App';

ReactDom.createRoot(document.getElementById('root')!).render(
  <App />
)
