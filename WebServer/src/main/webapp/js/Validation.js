function validateIDAsNumbers(str){
    const company_id = document.getElementById("company_id").value;
    if(parseInt(company_id)){
        return true;
    }
    document.getElementById("message_error").innerHTML = str + ' need to be a number';
    document.getElementById("message_error").className = 'message_error';

    return false;
}