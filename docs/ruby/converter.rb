require 'asciidoctor-pdf'

class PDFConverterCustom < (Asciidoctor::Converter.for 'pdf')
    register_for 'pdf'

    def convert_admonition node
        type = node.attr 'name'
        key_prefix = %(admonition_#{type}_)
        return super if (entries = theme.each_pair.select {|name, val| name.start_with? key_prefix }).empty?
        save_theme do
            entries.each {|name, val| theme[%(admonition_#{name.to_s.delete_prefix key_prefix})] = val }
            super
        end
    end


    def ink_title_page doc
        ink_title_page_logo
        ink_title_page_subtitle
        ink_title_page_main_title doc
        ink_title_page_authors doc
    end

    def convert_document doc
        output = super
        ink_content_page_headers doc
        output
    end

    private

    def ink_title_page_logo
        ipc_path   = ::File.expand_path '../resources/images/ipc.svg', __dir__
        return unless ::File.exist? ipc_path

        margin_top  = (theme.page_margin[0] || 72).to_f
        logo_x = page_width * 0.03
        logo_height = 72
        logo_top    = page_height - margin_top + logo_height * 0.05
        image ipc_path, at: [logo_x, logo_top], height: logo_height
    end

    def ink_title_page_subtitle
        move_cursor_to page_height * 0.68
        theme_font :title_page_subtitle do
            ink_prose 'Licenciatura em Engenharia Informática', align: :center, margin: 0
            move_down 8
            ink_prose 'Programação Aplicada', align: :center, margin: 0
        end
    end

    def ink_title_page_main_title doc
        move_cursor_to page_height * 0.50
        doctitle = doc.doctitle partition: true
        theme_font :title_page_title do
            ink_prose doctitle.main,
                align:       :center,
                color:       theme.title_page_title_font_color,
                line_height: 1.2,
                margin:      0
        end
    end

    def ink_title_page_authors doc
        move_cursor_to page_height * 0.30
            theme_font :title_page_authors do
                ink_prose 'Elaborado em: 2026/06/02', align: :center, margin: 0
                move_down 10
                date_text = "Atualizado em: #{doc.attr('revdate') || '2026/06/02'}"
                ink_prose date_text, align: :center, margin: 0
                move_down 25
                ink_prose '<strong>Nome e número do(s) Aluno(s):</strong>', align: :center, margin: 0
                move_down 6
                authors_list = doc.authors.map {|a| a.name }.join('<br>')
                ink_prose authors_list, align: :center, margin: 0
            end
    end

    def ink_content_page_headers doc
        logo_path = ::File.expand_path '../resources/images/estgoh.svg', __dir__
        return unless ::File.exist? logo_path

            header_params = build_header_params logo_path, doc
            (2..page_count).each do |pnum|
                go_to_page pnum
                ink_single_page_header header_params
            end
    end

    def build_header_params logo_path, doc
        margin_top  = (theme.page_margin[0] || 72).to_f
        margin_lr   = (theme.page_margin[1] || 72).to_f
        logo_height = 48
        {
            logo_path:   logo_path,
            margin_lr:   margin_lr,
            logo_height: logo_height,
            band_top: page_height - margin_top + logo_height + 20,
            title_y: page_height - margin_top + logo_height * 0.8,
            usable_w:    page_width - margin_lr * 2,
            title_text:  doc.doctitle || '',
        }
    end

    def ink_single_page_header p
        canvas do
            image p[:logo_path], at: [p[:margin_lr], p[:band_top]], height: p[:logo_height]

            font (theme.base_font_family || 'Lato'),
                size: (theme.header_font_size || 9), style: :normal do
                fill_color theme.header_font_color || '7F8C8D'
                text_box p[:title_text],
                    at:       [p[:margin_lr], p[:title_y]],
                    width:    p[:usable_w],
                    height:   14,
                    align:    :right,
                    overflow: :truncate
            end
        end
    end
end