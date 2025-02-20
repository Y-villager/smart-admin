import {getDownload} from "/@/lib/axios.js";

export const excelApi ={
    downloadFailedImportData:()=>{
        return getDownload('/excel/download_failed_data');
    },

    downloadExcel:(path)=>{
        return getDownload(`/excel/downloadExcel/${path}`);
    }
}
