import {getDownload} from "/@/lib/axios.js";

export const receivablesApi ={
    downloadFailedImportData:()=>{
        return getDownload('/excel/download_failed_data');
    }
}
