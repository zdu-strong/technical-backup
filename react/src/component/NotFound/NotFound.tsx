import { faHome } from '@fortawesome/free-solid-svg-icons';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { Button, Paper } from '@mui/material';
import { FormattedMessage } from 'react-intl';
import { Link } from 'react-router-dom';

export default (<div className="flex flex-col flex-auto justify-center items-center">
    <Paper className="flex flex-col justify-center" variant="outlined" style={{ padding: "1em", marginBottom: "1em" }}>
        <div className="flex justify-center" style={{ paddingBottom: "1em" }}>
            <FormattedMessage id="PageNotFound" defaultMessage="Not Found" />
        </div>
        <div>
            <Link to="/" >
                <Button
                    variant="contained"
                    startIcon={<FontAwesomeIcon icon={faHome} />}
                >
                    <FormattedMessage id="ReturnToHomePage" defaultMessage="To home" />
                </Button>
            </Link>
        </div>
    </Paper>
</div>)