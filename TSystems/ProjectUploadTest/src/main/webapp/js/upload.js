$(function () {
    $('#fileupload').fileupload({
        dataType: 'json',

        done: function (e, data) {
        	//$("tr:has(td)").remove();
            $.each(data.result, function (index, file) {

                $("#uploaded-files").append(
                		$('<tr/>')
                		.append($('<td/>').text(file.fileId))
                		.append($('<td/>').text(file.fileSize))
                        .append($('<td/>').html("<span href='rest/controller/delete/"+file.fileId+"/'>X</span>"))
                		)
            });
        },

		dropZone: $('#dropzone')
    });
    var dropZone = document.getElementById('dropzone');

    dropZone.ondragover = function() {
        this.className = 'drop';
        return false;
    }

    dropZone.ondragleave = function() {
        this.className = '';
        return false;
    }
});
