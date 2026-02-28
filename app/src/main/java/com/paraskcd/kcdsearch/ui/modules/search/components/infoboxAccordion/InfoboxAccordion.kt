package com.paraskcd.kcdsearch.ui.modules.search.components.infoboxAccordion

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import coil.request.ImageRequest
import com.paraskcd.kcdsearch.data.api.search.dataSources.infobox.InfoboxAttribute
import com.paraskcd.kcdsearch.ui.shared.components.expandableAccordionSection.ExpandableAccordionSection
import com.paraskcd.kcdsearch.ui.shared.components.expandableAccordionSection.ExpandableAccordionSectionParams

@Composable
fun InfoboxAccordion(
    params: InfoboxAccordionParams
) {
    val infobox = params.infobox

    ExpandableAccordionSection(
        params = ExpandableAccordionSectionParams(
            title = infobox.infobox.ifEmpty { infobox.title },
            modifier = Modifier.padding(bottom = 16.dp),
            initiallyExpanded = false,
            titleTextStyle = MaterialTheme.typography.titleSmall,
            summaryContent = { expanded ->
                if (infobox.content.isNotBlank()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = infobox.content
                            .replace(Regex("\\n{2,}"), "\n")
                            .trim(),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = if (expanded) Int.MAX_VALUE else 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            },
            contentPaddingValues = PaddingValues(top = 12.dp)
        )
    ) {
        if (infobox.imgSrc.isNotBlank()) {
            SubcomposeAsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(infobox.imgSrc)
                    .crossfade(true)
                    .build(),
                contentDescription = infobox.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 200.dp),
                contentScale = ContentScale.Fit
            ) {
                when (painter.state) {
                    is AsyncImagePainter.State.Success -> {
                        SubcomposeAsyncImageContent()
                    }
                    else -> {}
                }
            }
        }
        infobox.attributes.forEach { attr ->
            InfoboxAttributeRow(attribute = attr)
        }
        if (infobox.urls.isNotEmpty()) {
            infobox.urls.forEach { urlItem ->
                Text(
                    text = urlItem.title,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
private fun InfoboxAttributeRow(
    attribute: InfoboxAttribute,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = attribute.label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
        )
        attribute.value?.let { value ->
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        attribute.image?.let { img ->
            Spacer(modifier = Modifier.height(4.dp))
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(img.src)
                    .crossfade(true)
                    .build(),
                contentDescription = img.alt,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 180.dp),
                contentScale = ContentScale.Fit
            )
        }
    }
}