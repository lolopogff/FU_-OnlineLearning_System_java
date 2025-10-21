// Валидация цены на стороне клиента
function validatePrice(input) {
    const value = input.value;
    const priceRegex = /^\d{1,10}(\.\d{0,2})?$/;

    if (value === '' || value === '0') {
        input.classList.remove('error');
        return true;
    }

    if (!priceRegex.test(value) || parseFloat(value) < 0) {
        input.classList.add('error');
        return false;
    }

    input.classList.remove('error');
    return true;
}

// Валидация формы перед отправкой
document.getElementById('courseForm').addEventListener('submit', function (e) {
    const priceInput = document.getElementById('price');
    const titleInput = document.getElementById('title');
    let isValid = true;

    // Валидация названия
    if (titleInput.value.trim().length < 3) {
        titleInput.classList.add('error');
        isValid = false;
    } else {
        titleInput.classList.remove('error');
    }

    // Валидация цены
    if (!validatePrice(priceInput)) {
        isValid = false;
    }

    if (!isValid) {
        e.preventDefault();
        alert('Пожалуйста, исправьте ошибки в форме перед отправкой.');
    }
});

// Автоматическое форматирование цены
document.getElementById('price').addEventListener('blur', function (e) {
    const value = e.target.value;
    if (value && !isNaN(value) && value.trim() !== '') {
        const num = parseFloat(value);
        if (num >= 0) {
            e.target.value = num.toFixed(2);
        }
    }
});