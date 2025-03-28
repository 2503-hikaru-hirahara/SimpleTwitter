$(function() {
	$('#delete').on('click', function() {
		if (confirm("つぶやきの内容：\n" + $('#text').val() + "\n※本当に削除してよろしいですか?")) {
			return true;
		}
		return false;
	});
});
