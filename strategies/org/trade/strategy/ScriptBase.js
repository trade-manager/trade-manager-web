var ScriptBase = Class.create();
ScriptBase.prototype = {

    // Description: Helper base class.

    fErrorCount: 0,

    // Maximum number of errors before an exception is thrown
    fMaxErrorCount: 100,

    initialize: function() {

        this.initializeBase(this.type);
    },

    initializeBase: function(type) {

        // Initialize the result
        if (gs.nil(this.fResultData)) {

            this.fResultData = {
                log_messages: [],
                has_error: false
            };
        }

        if (gs.nil(type)){

             type = this.type;
        }

        this.logger = new global.GSLog(DEXConstants.PROPERTY.SCRIPT_LOG_LEVEL, type);

        // Adds timestamp to the message i.e.
        // 2022-07-07 15:58:59.869 Info: DEXMetricDataManager::saveMetricData  blah blah
        // this.logger.includeTimestamp();
        // this.logger.disableDatabaseLogs();
    },

    /**
     * Log a message
     *
     * @param level see global.GSLog.ERROR
     * @param message String
     */
    log: function(level, message) {

        if (global.GSLog.ERROR === level || 'error' === level) {

            if (!this.fResultData.hasOwnProperty('log_messages')) {

                this.fResultData.log_messages = [];
            }

            this.fErrorCount = this.fErrorCount + 1;
            this.fResultData.has_error = true;
            this.fResultData.log_messages.push(message);
            this.logger.logErr(message);

            if (this.fErrorCount > this.fMaxErrorCount) {
                throw gs.getMessage("Error: DEXScriptBase::log max error count exceeded: {0}", [this.fErrorCount]);
            }

        } else if (global.GSLog.WARNING === level) {

            this.logger.logWarning(message);

        } else if (global.GSLog.INFO === level) {

            this.logger.logInfo(message);
        } else if (global.GSLog.DEBUG === level) {

            this.logger.logDebug(message);
        } else {

            message = gs.getMessage("Error: DEXScriptBase::log unknown log level: {0}.", [level]);
            this.logger.logErr(message);
        }
    },

    /**
     * Get the result
     *
     * @returns this.fResultData
     */
    getResult: function() {

        return this.fResultData;
    },

    /**
     * Re-set the logs
     *
     */
    resetLogs: function() {

        this.fResultData.log_messages = [];
        this.fResultData.has_error = false;
    },

    type: 'ScriptBase'
};
